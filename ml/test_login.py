import urllib.request
import urllib.parse
import http.cookiejar

cj = http.cookiejar.CookieJar()
opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(cj))

# Test login
data = urllib.parse.urlencode({'email': 'aarav.sharma@smartteam.edu', 'password': 'password123'}).encode()
req = urllib.request.Request('http://localhost:8085/login', data=data)
resp = opener.open(req)
html = resp.read().decode('utf-8')
for line in html.splitlines():
    if 'class="err"' in line or 'class="ok"' in line or 'Invalid' in line:
        print('Alert in HTML:', line.strip())
print('Final URL:', resp.geturl())
