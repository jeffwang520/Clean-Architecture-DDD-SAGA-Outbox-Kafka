# Test Notes

1. Install Docker-Desktop
2. Install PostgreSql and PgAdmin
   user = postgres password = admin
3. mvn clean install
4. Grant full control access rights to users in folder infrastructure\docker-compose\volumes

5. Setup Zookeeper

   cd food-ordering-system\infrastructure\docker-compose
   docker compose -f common.yml -f zookeeper.yml up --remove-orphans

   // Check Zookeeper Status (WSL Only)
   echo ruok | ncat localhost 2181
      imok

6. Setup Kafka Cluster

   cd food-ordering-system\infrastructure\docker-compose
   docker-compose -f common.yml -f kafka_cluster.yml up

7. Setup Kafka Topics (Run ONLY once)

   cd food-ordering-system\infrastructure\docker-compose
   docker-compose -f common.yml -f init_kafka.yml up
   
8. Setup Kafka Manager

    http://localhost:9000
    
    Add Cluster => Save 
      - Cluster Name (food-ordering-system-cluster) 
      - Cluster Zookeeper Hosts (zookeeper:2181)  

9. Run containers from IDE
   - customer-service
   - order-service
   - payment-service
   - restaurant-service
  
10. Postman
   - Customer
     * New Customer #1
     * New Customer #2
     * 
    - Order - Step 1
      - Post an Order - Approved
      - Tracking Order
    - Order - Step 2
      - Post an Order - Cancelled
      - Tracking Order
      - 
11. PgAdmin - Check Databases
   - Customer
   - Order
   - Payment
   - Restaurant

12. Shutdown (IDE)
   - Services
   - Kafka Cluster (Cntl + C)
   - Zookeeper (Cntl + C)
