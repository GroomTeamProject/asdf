resource "aws_ecs_task_definition" "order_service_task" {
  family                   = "order-service"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_execution_role.arn

  container_definitions = jsonencode([
    {
      name      = "order-service"
      image     = "docker.io/wsk221e/team02-order-service:latest"
      essential = true
      portMappings = [
        {
          containerPort = 8085
          hostPort      = 8085
        }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = "/ecs/order-service"
          "awslogs-region"        = "ap-northeast-2"
          "awslogs-stream-prefix" = "ecs"
          "awslogs-create-group"  = "true"
        }
      }
      environment = [
        {
          name  = "SPRING_PROFILES_ACTIVE"
          value = "dev"
        },
        {
          name  = "SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE"
          value = "10"
        },
        {
          name  = "SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE"
          value = "2"
        },
        {
          name  = "SPRING_DATASOURCE_HIKARI_CONNECTION_TIMEOUT"
          value = "10000"
        },
        {
          name  = "SPRING_DATASOURCE_HIKARI_IDLE_TIMEOUT"
          value = "60000"
        },
        {
          name  = "SPRING_DATASOURCE_HIKARI_MAX_LIFETIME"
          value = "300000"
        }
      ]
      secrets = [
        {
          name      = "ORDER_SERVICE_DB_URL"
          valueFrom = "${data.aws_secretsmanager_secret.team02_secret.arn}:ORDER_SERVICE_DB_URL::"
        },
        {
          name      = "ORDER_SERVICE_DB_USERNAME"
          valueFrom = "${data.aws_secretsmanager_secret.team02_secret.arn}:ORDER_SERVICE_DB_USERNAME::"
        },
        {
          name      = "ORDER_SERVICE_DB_PASSWORD"
          valueFrom = "${data.aws_secretsmanager_secret.team02_secret.arn}:ORDER_SERVICE_DB_PASSWORD::"
        },
        {
          name      = "KAFKA_BOOTSTRAP_SERVERS"
          valueFrom = "${data.aws_secretsmanager_secret.team02_secret.arn}:KAFKA_BOOTSTRAP_SERVERS::"
        },
        {
          name      = "MSA_GATEWAY_URL"
          valueFrom = "${data.aws_secretsmanager_secret.team02_secret.arn}:MSA_GATEWAY_URL::"
        },
        {
          name      = "JWT_SECRET"
          valueFrom = "${data.aws_secretsmanager_secret.team02_secret.arn}:JWT_SECRET::"
        }
      ]
    }
  ])
}

resource "aws_service_discovery_service" "order_service" {
  name = "order-service"

  dns_config {
    namespace_id = aws_service_discovery_private_dns_namespace.team02_namespace.id

    dns_records {
      ttl  = 10
      type = "A"
    }

    routing_policy = "MULTIVALUE"
  }

  health_check_custom_config {
    failure_threshold = 1
  }
}

resource "aws_ecs_service" "order_service" {
  name            = "order-service"
  cluster         = aws_ecs_cluster.team02_cluster.id
  task_definition = aws_ecs_task_definition.order_service_task.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = [aws_subnet.team02_public_subnet_a.id]
    security_groups  = [aws_security_group.team02_services_security_group.id]
    assign_public_ip = true
  }

  service_registries {
    registry_arn = aws_service_discovery_service.order_service.arn
  }
}
