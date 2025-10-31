# Services Security Group
resource "aws_security_group" "team02_services_security_group" {
  name        = "team02-services-security-group"
  description = "Allow HTTP to Services"
  vpc_id      = aws_vpc.team02_vpc.id

  ingress {
    from_port   = 8080
    to_port     = 8089
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "team02-services-security-group"
  }
}

# Gateway Security Group
resource "aws_security_group" "team02_gateway_security_group" {
  name        = "team02-gateway-security-group"
  description = "Security group for Gateway service"
  vpc_id      = aws_vpc.team02_vpc.id

  # 외부에서 게이트웨이 접근 허용
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow HTTP traffic to Gateway"
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "team02-gateway-security-group"
  }
}

# RDS Security Group
resource "aws_security_group" "team02_rds_security_group" {
  name        = "team02-rds-security-group"
  description = "Security group for RDS MariaDB"
  vpc_id      = aws_vpc.team02_vpc.id

  # ECS 서비스들로부터의 접근 허용
  ingress {
    from_port = 3306
    to_port   = 3306
    protocol  = "tcp"
    security_groups = [
      aws_security_group.team02_services_security_group.id
    ]
    description = "Allow from ECS services"
  }

  # 로컬/외부에서 접근 허용 (개발용)
  ingress {
    from_port   = 3306
    to_port     = 3306
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow from anywhere (dev only)"
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "team02-rds-security-group"
  }
}
