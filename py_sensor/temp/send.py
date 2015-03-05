
import sys
sys.path.append('.')

import time
import socket 
import messages_pb2
import struct
import random
from datetime import datetime

def send_message(sock, message):
    """ Send a serialized message (protobuf Message interface)
    to a socket, prepended by its length packed in 4
    bytes (big endian).
    """
    s = message.SerializeToString()
    packed_len = struct.pack('>L', len(s))
    sock.sendall(packed_len)
    sock.sendall(s)

def build_msg():
    m = messages_pb2.Measurement()
    m.mid = 1
    m.value = random.randint(-10, 40)
    m.time = datetime.utcnow().isoformat()
    return m

socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
socket.connect(("127.0.0.1", 6767))

while True:
    send_message(socket, build_msg())
    time.sleep(2)

socket.close()
