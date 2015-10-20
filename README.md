# rest-spring-boot-example
Example showing the usage of RESTful-Webservice in a Spring-Boot Context using a simple Singleton In-Memory Datastructure  and utilizing Unit- and Integrationtests

The Project shows how to implement a REST-Service with Spring-Boot including Unit and Integration-Tests. 

It uses a simple singleton based repository to store transaction-objects as well as childs and offers 4 REST-Services to access and store the data:

Add new transaction
-------------------
PUT /transactionservice/transaction/{id}
Body: { "amount": double, "type": string, "parent_id": long }

transaction_id is a long specifying a new transaction
amount is a double specifying the amount
type is a string specifying a type of the transaction.
parent_id is an optional long that may specify the parent transaction of this transaction.

Get Transaction by ID
---------------------
GET /transactionservice/transaction/{id}
Returns: { "amount": double, "type": string, "parent_id": long }

Get Transaction-IDs by type 
---------------------------
GET /transactionservice/types/{type}
Returns: [ long, long, .... ]
A json list of all transaction ids that share the same type.

Get Transaction Sums
--------------------
GET /transactionservice/sum/{id}
Returns: { "sum", double }
Get the Sum of all transaction that are transitively linked by their parent_id to transaction_id
