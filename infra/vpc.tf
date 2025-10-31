# VPC
resource "aws_vpc" "team02_vpc" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support   = true
  
  tags = {
    Name = "team02-vpc"
  }
}

# Subnet A
resource "aws_subnet" "team02_public_subnet_a" {
  vpc_id            = aws_vpc.team02_vpc.id
  cidr_block        = "10.0.1.0/24"
  availability_zone = "ap-northeast-2a"
  map_public_ip_on_launch = true
  tags = {
    Name = "team02-public-a"
  }
}

# Subnet A Route Table
resource "aws_route_table_association" "team02_public_a_association" {
  subnet_id      = aws_subnet.team02_public_subnet_a.id
  route_table_id = aws_route_table.team02_public_route_table.id
}

# Subnet B
resource "aws_subnet" "team02_public_subnet_b" {
  vpc_id                  = aws_vpc.team02_vpc.id
  cidr_block              = "10.0.2.0/24"
  availability_zone       = "ap-northeast-2b"
  map_public_ip_on_launch = true

  tags = {
    Name = "team02-public-b"
  }
}

# Subnet B Route Table
resource "aws_route_table_association" "team02_public_b_association" {
  subnet_id      = aws_subnet.team02_public_subnet_b.id
  route_table_id = aws_route_table.team02_public_route_table.id
}

# Gateway
resource "aws_internet_gateway" "team02_internet_gateway" {
  vpc_id = aws_vpc.team02_vpc.id
  tags = {
    Name = "team02-internet-gateway"
  }
}

resource "aws_route_table" "team02_public_route_table" {
  vpc_id = aws_vpc.team02_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.team02_internet_gateway.id
  }

  tags = {
    Name = "team02-public-route-table"
  }
}

