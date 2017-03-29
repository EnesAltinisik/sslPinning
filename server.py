import socket, ssl

bindsocket = socket.socket()

bindsocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
bindsocket.bind(('', 10023))
bindsocket.listen(5)
print "creted new"

i=0

while True:
    
      
    try:
        newsocket, fromaddr = bindsocket.accept()
        
        wrappedSocket= ssl.wrap_socket(newsocket,
                                         server_side=True,
                                 certfile="enes.crt",
                                 keyfile="enes.key")
        try:
            wrappedSocket.send(str(i))
            i+=1;
            
        finally:
            wrappedSocket.close()
            newsocket.close();
    except:
        print "stop"