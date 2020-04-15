# Python file: updateip.py - updates the mySQL db with this Rsapberry Pi IP
# In order to do this everytime the raspberry goes online:
# At terminal: crontab -e
# Insert in the text file: @reboot python PATH/updateip.py

import MySQLdb
import socket
import time
from urllib2 import urlopen

# Connect to database
db = MySQLdb.connect(host="db.ist.utl.pt", user="ist181216", passwd="xsxn7474", db="ist181216")

cur = db.cursor()

# Get name and public ip address
name = socket.gethostname()
address = urlopen('http://ip.42.pl/raw').read()

# Delete this raspberry if it exists in the database
cur.execute("DELETE FROM PiAddress WHERE name = %s and ipAddress = INET_ATON(%s)", [name, address])

# Insert it again in the database with updated ip
cur.execute("INSERT INTO PiAddress( ipAddress, name, lastUpdate ) VALUES (INET_ATON(%s),%s, NOW())", [address, name]) 

# Commit changes to database
db.commit()

# Close cursor and database
cur.close()
db.close()

