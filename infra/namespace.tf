# Service Discovery Namespace (VPC 내부 DNS)
resource "aws_service_discovery_private_dns_namespace" "team02_namespace" {
  name = "team02.local"
  vpc  = aws_vpc.team02_vpc.id

  tags = {
    Name = "team02-namespace"
  }
}
