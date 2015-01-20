
def send_message(sock, message):
    """ Send a serialized message (protobuf Message interface)
    to a socket, prepended by its length packed in 4
    bytes (big endian).
    """
    s = message.SerializeToString()
    packed_len = struct.pack('>L', len(s))
    sock.sendall(packed_len + s)
    

import sys

sys.path.append('.')

import socket 
import messages_pb2
import struct

socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
socket.connect(("127.0.0.1", 6767))

m = messages_pb2.Measurement()
m.mid = 1
m.value = 22.0
m.time = "NOW"

send_message(socket, m)

socket.close()
