resource "aws_ecs_task_definition" "gateway_task" {
  family                   = "gateway"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_execution_role.arn

  container_definitions = jsonencode([
    {
      name      = "gateway"
      image     = "docker.io/wsk221e/team02-gateway:latest"
      essential = true
      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
        }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = "/ecs/gateway"
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
      ]
      secrets = [
        {
          name  = "USER_SERVICE_URL"
          valueFrom = "${data.aws_secretsmanager_secret.team02_secret.arn}:USER_SERVICE_URL::"
        },
        {
          name  = "OWNER_SERVICE_URL"
          valueFrom = "${data.aws_secretsmanager_secret.team02_secret.arn}:OWNER_SERVICE_URL::"
        },
        {
          name  = "ORDER_SERVICE_URL"
          valueFrom = "${data.aws_secretsmanager_secret.team02_secret.arn}:ORDER_SERVICE_URL::"
        },
        {
          name  = "REVIEW_SERVICE_URL"
          valueFrom = "${data.aws_secretsmanager_secret.team02_secret.arn}:REVIEW_SERVICE_URL::"
        },
        {
          name  = "NOTIFICATION_SERVICE_URL"
          valueFrom = "${data.aws_secretsmanager_secret.team02_secret.arn}:NOTIFICATION_SERVICE_URL::"
        },
        {
          name  = "DELIVERY_SERVICE_URL"
          valueFrom = "${data.aws_secretsmanager_secret.team02_secret.arn}:DELIVERY_SERVICE_URL::"
        },
      ]
    }
  ])
}

resource "aws_service_discovery_service" "gateway_service" {
  name = "gateway"

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

resource "aws_ecs_service" "gateway" {
  name            = "gateway"
  cluster         = aws_ecs_cluster.team02_cluster.id
  task_definition = aws_ecs_task_definition.gateway_task.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = [aws_subnet.team02_public_subnet_a.id]
    security_groups  = [aws_security_group.team02_gateway_security_group.id]
    assign_public_ip = true
  }

  service_registries {
    registry_arn = aws_service_discovery_service.gateway_service.arn
  }
}

