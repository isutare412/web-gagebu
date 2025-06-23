#/bin/bash

openssl genrsa -out private_key.pem 2048
openssl pkcs8 -topk8 -inform PEM -outform PEM -in private_key.pem -nocrypt -out private_key_pkcs8.pem
openssl rsa -in private_key.pem -pubout -out public_key.pem
rm private_key.pem
mv private_key_pkcs8.pem private_key.pem
