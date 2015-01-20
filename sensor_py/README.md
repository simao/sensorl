
To generate proto

protoc --python_out=$(pwd) --proto_path=../src/main/scala/message ../src/main/scala/message/messages.proto

