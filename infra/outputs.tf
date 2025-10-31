# RDS Outputs
output "rds_endpoint" {
  description = "RDS instance endpoint"
  value       = aws_db_instance.team02_mariadb.endpoint
}

output "mysql_connect_command" {
  description = "MySQL command to connect to RDS"
  value       = "mysql -h ${split(":", aws_db_instance.team02_mariadb.endpoint)[0]} -u ${var.db_username} -p"
}

output "rds_connection_string" {
  description = "JDBC connection string for Spring Boot"
  value       = "jdbc:mariadb://${aws_db_instance.team02_mariadb.endpoint}/{DB_NAME}?characterEncoding=UTF-8&useUnicode=true&serverTimezone=Asia/Seoul"
}

# Kafka Outputs  
output "kafka_public_ip" {
  description = "Kafka EC2 Public IP"
  value       = aws_instance.kafka.public_ip
}

output "kafka_private_ip" {
  description = "Kafka EC2 Private IP"
  value       = aws_instance.kafka.private_ip
}

output "kafka_ui_url" {
  description = "Kafka UI URL"
  value       = "http://${aws_instance.kafka.public_ip}:9001"
}

output "kafka_bootstrap_servers" {
  description = "Kafka Bootstrap Servers (for ECS services)"
  value       = "kafka.team02.local:9092"
}

output "kafka_ssh_command" {
  description = "SSH command to connect to Kafka EC2"
  value       = "ssh -i ~/.ssh/ec2_key_kafka.pem ubuntu@${aws_instance.kafka.public_ip}"
}

# ECS Services Outputs
output "gateway_service_discovery" {
  description = "Gateway Service Discovery DNS"
  value       = "gateway.team02.local:8080"
}

output "order_service_discovery" {
  description = "Order Service Discovery DNS"
  value       = "order-service.team02.local:8085"
}

output "user_service_discovery" {
  description = "User Service Discovery DNS"
  value       = "user-service.team02.local:8082"
}

output "owner_service_discovery" {
  description = "Owner Service Discovery DNS"
  value       = "owner-service.team02.local:8081"
}

output "review_service_discovery" {
  description = "Review Service Discovery DNS"
  value       = "review-service.team02.local:8086"
}

output "notification_service_discovery" {
  description = "Notification Service Discovery DNS"
  value       = "notification-service.team02.local:8088"
}
