resource "aws_ecs_task_definition" "user_service_task" {
  family                   = "user-service"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_execution_role.arn

  container_definitions = jsonencode([
    {
      name      = "user-service"
      image     = "docker.io/wsk221e/team02-user-service:latest"
      essential = true
      portMappings = [
        {
          containerPort = 8082
          hostPort      = 8082
        }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = "/ecs/user-service"
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
          name      = "USER_SERVICE_DB_URL"
          valueFrom = "${data.aws_secretsmanager_secret.team02_secret.arn}:USER_SERVICE_DB_URL::"
        },
        {
          name      = "USER_SERVICE_DB_USERNAME"
          valueFrom = "${data.aws_secretsmanager_secret.team02_secret.arn}:USER_SERVICE_DB_USERNAME::"
        },
        {
          name      = "USER_SERVICE_DB_PASSWORD"
          valueFrom = "${data.aws_secretsmanager_secret.team02_secret.arn}:USER_SERVICE_DB_PASSWORD::"
        },
        {
          name      = "KAFKA_BOOTSTRAP_SERVERS"
          valueFrom = "${data.aws_secretsmanager_secret.team02_secret.arn}:KAFKA_BOOTSTRAP_SERVERS::"
        },
        {
          name      = "JWT_SECRET"
          valueFrom = "${data.aws_secretsmanager_secret.team02_secret.arn}:JWT_SECRET::"
        }
      ]
    }
  ])
}

resource "aws_service_discovery_service" "user_service" {
  name = "user-service"

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

resource "aws_ecs_service" "user_service" {
  name            = "user-service"
  cluster         = aws_ecs_cluster.team02_cluster.id
  task_definition = aws_ecs_task_definition.user_service_task.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = [aws_subnet.team02_public_subnet_a.id]
    security_groups  = [aws_security_group.team02_services_security_group.id]
    assign_public_ip = true
  }

  service_registries {
    registry_arn = aws_service_discovery_service.user_service.arn
  }
}

