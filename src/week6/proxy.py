#! /usr/bin/env python

#
# Hacking TCP proxy
# sudo ip6tables -A OUTPUT -p icmpv6 -d 2001:67c:2564:a170:a00:27ff:fe11:cecb -j DROP
#

from scapy.all import *
from socket import *
from struct import *
from threading import Thread

import time
 
EXTERNAL_IP = "2001:67c:2564:a170:a00:27ff:fe11:cecb"
LISTEN_IP = "127.0.0.1"
LISTEN_PORT = 1234
RECV_BUFFER = 1024

clientsock = None

scapysock = conf.L2socket()

class SniffThread (Thread):
	SNIFF_FILTER = "ip6 and host " + EXTERNAL_IP

	def __init__ (self, cb):
		self.__cb = cb
		Thread.__init__(self)

	def run (self):
		sniff(filter=self.SNIFF_FILTER, prn=self.callback)

	def callback (self, p):
		if IPv6 in p and (p[IPv6].src == EXTERNAL_IP):
			self.__cb(p)
			print("Packet sniffed")

class CommunicationThread (Thread):
	def run (self):
		serversock = socket(AF_INET, SOCK_STREAM)
		serversock.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
		serversock.bind((LISTEN_IP, LISTEN_PORT))
		serversock.listen(5)

		while True:
			global clientsock
			print("Waiting for client...")
			sock, addr = serversock.accept()
			clientsock = sock
			print("Client connected!")

			data_so_far = ""

			while True:
				data = sock.recv(RECV_BUFFER)
				if not data: break
				data_so_far += data

				while len(data_so_far) >= 4:
					n, = unpack(">i", data_so_far[:4])
					if len(data_so_far) >= (n + 4):
						print("Sending packet, data left: " + str(len(data_so_far) - (n + 4)))
						put_packet_on_wire(data_so_far[4:n+4])
						data_so_far = data_so_far[n+4:]
					else:
						break

			clientsock = None

def put_packet_on_wire(data):
	scapysock.send(Ether()/IPv6(data))

def packet_sniffed(p):
	global clientsock
	packetstring = str(p[IPv6])
	if clientsock is not None:
		clientsock.send(pack(">i", len(packetstring)) + packetstring)

commsthread = CommunicationThread()
commsthread.daemon = True
commsthread.start()

sniffthread = SniffThread(packet_sniffed)
sniffthread.daemon = True
sniffthread.start()

while True:
	time.sleep(1)
