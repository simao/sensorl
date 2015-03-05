#!/usr/bin/python
# Can enable debug output by uncommenting:
#import logging
#logging.basicConfig(level=logging.DEBUG)

import time
import sys
sys.path.append('.')
sys.path.append('..')
import socket
import messages_pb2
import struct
from datetime import datetime

from TSL2561 import TSL2561

sensor = TSL2561()

def send_message(sock, message):
    """ Send a serialized message (protobuf Message interface)
    to a socket, prepended by its length packed in 4
    bytes (big endian).
    """
    s = message.SerializeToString()
    packed_len = struct.pack('>L', len(s))
    sock.sendall(packed_len)
    sock.sendall(s)

def build_msg(lux):
    m = messages_pb2.Measurement()
    m.mid = 0
    m.value = lux
    m.time = datetime.utcnow().isoformat()
    m.key = "lux"
    return m

print sys.argv

socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
socket.connect((sys.argv[1], int(sys.argv[2])))

while True:
    lux = sensor.readLux()
    send_message(socket, build_msg(lux))
    print 'Lux: {0:0.3F}'.format(lux)
    time.sleep(5.0)

socket.close()
