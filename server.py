import BaseHTTPServer, SimpleHTTPServer
import ssl

HTTPServer = BaseHTTPServer.HTTPServer(('', 1234), SimpleHTTPServer.SimpleHTTPRequestHandler)
HTTPServer.socket = ssl.wrap_socket (HTTPServer.socket,keyfile='enes.key', certfile='enes.crt', server_side=True)
HTTPServer.serve_forever()