package com.smartteam.service;

import com.smartteam.entity.Rating;
import com.smartteam.entity.Student;
import com.smartteam.repository.RatingRepository;
import com.smartteam.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;

@Service
public class MatchingService {

    private final StudentRepository studentRepo;
    private final RatingRepository ratingRepo;
    private final RestClient mlClient;

    public MatchingService(StudentRepository studentRepo, RatingRepository ratingRepo, RestClient.Builder builder) {
        this.studentRepo = studentRepo;
        this.ratingRepo = ratingRepo;
        this.mlClient = builder.baseUrl("http://127.0.0.1:8000").build();
    }

    public List<Map<String, Object>> recommend(String requiredSkills, String interest, String availability, String experience) {
        return recommend(requiredSkills, interest, availability, experience, null);
    }

    public List<Map<String, Object>> recommend(String requiredSkills, String interest, String availability, String experience, Long excludeStudentId) {
        List<String> required = Arrays.stream(requiredSkills.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(String::toLowerCase)
                .toList();

        List<Map<String, Object>> result = new ArrayList<>();
        List<Student> students = studentRepo.findByActiveTrue();

        boolean hasOtherStudents = students.stream().anyMatch(s -> excludeStudentId == null || !s.getId().equals(excludeStudentId));

        for (Student s : students) {
            if (hasOtherStudents && excludeStudentId != null && s.getId().equals(excludeStudentId)) {
                continue; // Do not recommend user themselves when other candidate students exist
            }

            double skill = localSkillScore(required, s);
            double interestScore = containsIgnoreCase(s.getInterests(), interest) ? 100.0 : 30.0;
            double avail = containsIgnoreCase(s.getAvailability(), availability) ? 100.0 : 40.0;
            double exp = experience.equalsIgnoreCase(s.getExperienceLevel()) ? 100.0 : 60.0;

            // Soft skill features derived from actual teammate peer reviews
            double commScore = calculateCommunicationScore(s.getId());
            double relScore = calculateReliabilityScore(s.getId());

            Map<String, Object> featurePayload = Map.of(
                    "skill_match", skill,
                    "interest_match", interestScore,
                    "availability", avail,
                    "experience", exp,
                    "communication", commScore,
                    "reliability", relScore
            );

            Map<String, Object> mlResult = callMlPredict(featurePayload);
            double mlProb = ((Number) mlResult.get("mlProbability")).doubleValue();
            double compScore = ((Number) mlResult.get("compatibilityScore")).doubleValue();

            // Calibrated composite score combining regressor compatibility, classifier probability, and skill match
            double finalScore = 0.55 * compScore + 0.30 * skill + 0.15 * mlProb;

            Map<String, Object> candidate = new LinkedHashMap<>();
            candidate.put("studentId", s.getId());
            candidate.put("name", s.getName());
            candidate.put("department", s.getDepartment());
            candidate.put("studyYear", s.getStudyYear());
            candidate.put("experience", s.getExperienceLevel());
            candidate.put("availability", s.getAvailability());
            candidate.put("interests", s.getInterests());
            candidate.put("skillMatch", round(skill));
            candidate.put("mlScore", round(mlProb));
            candidate.put("compatibilityScore", round(compScore));
            candidate.put("matchScore", round(finalScore));
            candidate.put("label", mlResult.get("label"));
            candidate.put("archetype", mlResult.get("archetype"));
            candidate.put("archetypeDescription", mlResult.get("archetypeDescription"));
            candidate.put("communicationScore", round(commScore));
            candidate.put("reliabilityScore", round(relScore));

            result.add(candidate);
        }

        result.sort((a, b) -> Double.compare((Double) b.get("matchScore"), (Double) a.get("matchScore")));
        return result.stream().limit(10).toList();
    }

    private double localSkillScore(List<String> required, Student s) {
        if (required.isEmpty()) return 0;
        String all = ((s.getBio() == null ? "" : s.getBio()) + " " +
                (s.getInterests() == null ? "" : s.getInterests()) + " " +
                (s.getDepartment() == null ? "" : s.getDepartment())).toLowerCase();
        long hit = required.stream().filter(k -> all.contains(k.toLowerCase().trim())).count();
        return 100.0 * hit / required.size();
    }

    private boolean containsIgnoreCase(String text, String needle) {
        return text != null && needle != null && !needle.isBlank() && text.toLowerCase().contains(needle.toLowerCase());
    }

    private double calculateCommunicationScore(Long studentId) {
        List<Rating> ratings = ratingRepo.findByToStudentIdOrderByIdDesc(studentId);
        if (ratings == null || ratings.isEmpty()) {
            return 75.0; // Baseline prior for new student
        }
        double avg = ratings.stream().mapToInt(Rating::getTeamworkScore).average().orElse(3.75);
        return Math.min(100.0, Math.max(10.0, (avg / 5.0) * 100.0));
    }

    private double calculateReliabilityScore(Long studentId) {
        List<Rating> ratings = ratingRepo.findByToStudentIdOrderByIdDesc(studentId);
        if (ratings == null || ratings.isEmpty()) {
            return 75.0; // Baseline prior for new student
        }
        double avg = ratings.stream().mapToInt(Rating::getReliabilityScore).average().orElse(3.75);
        return Math.min(100.0, Math.max(10.0, (avg / 5.0) * 100.0));
    }

    private Map<String, Object> callMlPredict(Map<String, Object> features) {
        try {
            Map<?, ?> response = mlClient.post()
                    .uri("/predict")
                    .body(features)
                    .retrieve()
                    .body(Map.class);

            if (response != null) {
                Map<String, Object> result = new HashMap<>();
                if (response.get("probability") instanceof Number n) {
                    result.put("mlProbability", n.doubleValue() * 100.0);
                } else {
                    result.put("mlProbability", 50.0);
                }

                if (response.get("compatibility_score") instanceof Number n) {
                    result.put("compatibilityScore", n.doubleValue());
                } else {
                    result.put("compatibilityScore", result.get("mlProbability"));
                }

                Object label = response.get("label");
                result.put("label", label != null ? label.toString() : "COMPATIBLE");

                Object archetype = response.get("archetype");
                result.put("archetype", archetype != null ? archetype.toString() : "Balanced Generalist");

                Object archetypeDesc = response.get("archetype_description");
                result.put("archetypeDescription", archetypeDesc != null ? archetypeDesc.toString() : "");

                return result;
            }
        } catch (Exception ignored) {
            // Graceful heuristic fallback if ML microservice is unreachable
        }

        double skill = ((Number) features.getOrDefault("skill_match", 50.0)).doubleValue();
        double interest = ((Number) features.getOrDefault("interest_match", 50.0)).doubleValue();
        double avail = ((Number) features.getOrDefault("availability", 50.0)).doubleValue();
        double exp = ((Number) features.getOrDefault("experience", 50.0)).doubleValue();
        double comm = ((Number) features.getOrDefault("communication", 70.0)).doubleValue();
        double rel = ((Number) features.getOrDefault("reliability", 70.0)).doubleValue();

        double fallbackScore = 0.38 * skill + 0.18 * interest + 0.15 * avail + 0.13 * exp + 0.09 * comm + 0.07 * rel;

        Map<String, Object> fallback = new HashMap<>();
        fallback.put("mlProbability", fallbackScore);
        fallback.put("compatibilityScore", fallbackScore);
        fallback.put("label", fallbackScore >= 60.0 ? "COMPATIBLE" : "LOW_COMPATIBILITY");
        fallback.put("archetype", skill >= 60.0 ? "High-Skill Specialist" : "Balanced Generalist");
        fallback.put("archetypeDescription", "Determined via multi-attribute candidate heuristic");
        return fallback;
    }

    private double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
