### Simple Blockchain Lite
This is a simple blockchain implementation in Java. It is a lite version of a blockchain, which is a distributed database that maintains a continuously growing list of ordered records called blocks. Each block contains a timestamp and a link to a previous block.
#### prerequisites
- Java 17
- Maven
- Git
- IDE (IntelliJ IDEA, Eclipse, etc.)
- LevelDB
- Postman
#### Installation
1. Clone the repository
```bash
git clone https://github.com/zhtswang/simple-blockchain-lite.git
```
2. Open the project in your IDE
3. Maven Compile the project and generate related GRPC files
```bash
mvn compile
```
4. Run the project
```bash
mvn package
``` 
5. Run the server, you can configure the server at the application.yml file and run the following command by the configuration
```bash
echo "Starting Node 1"
java -Dnode.id=1 -jar simple-blockchain-0.1.1.jar --server.port=8080
eho "Starting Node 2"
java -Dnode.id=2 -jar simple-blockchain-0.1.1.jar --server.port=8081
echo "Starting Node 3"
java -Dnode.id=3 -jar simple-blockchain-0.1.1.jar --server.port=8082
```
6. Test the server by Postman
- Send Transactions to Node.
```bash
curl --location 'localhost:8081/transactions/broadcast' \
--header 'Content-Type: application/json' \
--data '[
    {
        "header": {},
        "payload": {
            "smartContract": "test/v1",
            "args": [
                "write",
                "testkey",
                "testvalue"
            ]
        }
    }
]'
```
7. Query the blockchain
```bash
curl --location 'localhost:8082/api/v1/blockchain/node/1/block?from=0&to=5'
```

<i>Notice: The project is in progress, it has many issues and can be only used to study the block
chain knowledge.</i>