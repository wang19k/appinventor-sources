import urllib
import urllib2
import sys

def main(ipaddr):
    sys.stdout.write('==> ')
    line = sys.stdin.readline().strip()
    while True:
        if line == 'quit':
            break
        code = urllib.quote_plus(line)
        v = urllib2.urlopen('http://%s:8001/_eval?code=%s' % (ipaddr, code))
        sys.stdout.write(v.read() + '\n')
        sys.stdout.write('==> ')
        line = sys.stdin.readline().strip()

if __name__ == '__main__':
    main(sys.argv[1])

