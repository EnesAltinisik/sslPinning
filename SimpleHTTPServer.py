
__version__ = "0.6"

__all__ = ["SimpleHTTPRequestHandler"]

import BaseHTTPServer
import sys
try:
    from cStringIO import StringIO
except ImportError:
    from StringIO import StringIO


class SimpleHTTPRequestHandler(BaseHTTPServer.BaseHTTPRequestHandler):
    count=0;
    def do_GET(self):
        """Serve a GET request."""
        stringBuffer = self.create_head()
        if stringBuffer:
            try:
                while 1:
                    bufForWrite = stringBuffer.read(1024)
                    if not bufForWrite:
                        break
                    self.wfile.write(bufForWrite)
            finally:
                stringBuffer.close()

    def create_head(self):
        
        stringBuffer = StringIO()

        stringBuffer.write(str(SimpleHTTPRequestHandler.count))
        SimpleHTTPRequestHandler.count=SimpleHTTPRequestHandler.count+1
        length = stringBuffer.tell()
        stringBuffer.seek(0)
        self.send_response(200)
        encoding = sys.getfilesystemencoding()
        self.send_header("Content-type", "text/html; charset=%s" % encoding)
        self.send_header("Content-Length", str(length))
        self.end_headers()
        return stringBuffer
