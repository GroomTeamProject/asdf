# RDS Instance - MariaDB
resource "aws_db_instance" "team02_mariadb" {
  identifier     = "team02-mariadb"
  engine         = "mariadb"
  engine_version = "11.4"

  instance_class    = "db.t3.micro"
  allocated_storage = 20
  storage_type      = "gp3"

  username = var.db_username
  password = var.db_password

  # 인코딩 설정이 적용된 파라미터 그룹
  parameter_group_name = aws_db_parameter_group.team02_mariadb_params.name

  # 네트워크 설정
  db_subnet_group_name   = aws_db_subnet_group.team02_db_subnet_group.name
  vpc_security_group_ids = [aws_security_group.team02_rds_security_group.id]
  
  # RDS 공개 접근 (개발용)
  publicly_accessible = true

  # 백업 설정
  backup_retention_period = 7
  backup_window           = "03:00-04:00"
  maintenance_window      = "mon:04:00-mon:05:00"

  # 삭제 보호 비활성화 (개발 환경)
  skip_final_snapshot = true
  deletion_protection = false

  tags = {
    Name        = "team02-mariadb"
    Environment = "dev"
  }
}

# RDS Subnet Group
resource "aws_db_subnet_group" "team02_db_subnet_group" {
  name = "team02-db-subnet-group"
  subnet_ids = [
    aws_subnet.team02_public_subnet_a.id,
    aws_subnet.team02_public_subnet_b.id
  ]

  tags = {
    Name = "team02-db-subnet-group"
  }
}

