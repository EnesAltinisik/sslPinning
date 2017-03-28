import socket, ssl

bindsocket = socket.socket()
bindsocket.bind(('', 10023))
bindsocket.listen(5)

def do_something(connstream, data):
    print "do_something:", data
    return True

def deal_with_client(connstream):
    data = connstream.read()
    while data:
        if not do_something(connstream, data):
            break
        data = connstream.read()

while True:
    newsocket, fromaddr = bindsocket.accept()
    
    wrappedSocket= ssl.wrap_socket(newsocket,
                                 server_side=True,
                                 certfile="enes.crt",
                                 keyfile="enes.key")
    
    try:
            wrappedSocket.sendall("deneme")
            
    finally:
        wrappedSocket.close()
        newsocket.close();