# Terraform Backend Infrastructure
# S3 버킷과 DynamoDB 테이블을 Terraform으로 관리

# S3 버킷 (Terraform State 저장용)
resource "aws_s3_bucket" "terraform_state" {
  bucket = "team02-terraform-state-bucket"

  tags = {
    Name        = "Terraform State Bucket"
    Environment = "dev"
    Purpose     = "terraform-state"
  }
}

# S3 버킷 버전 관리
resource "aws_s3_bucket_versioning" "terraform_state" {
  bucket = aws_s3_bucket.terraform_state.id
  versioning_configuration {
    status = "Enabled"
  }
}

# S3 버킷 암호화
resource "aws_s3_bucket_server_side_encryption_configuration" "terraform_state" {
  bucket = aws_s3_bucket.terraform_state.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

# S3 버킷 공개 액세스 차단
resource "aws_s3_bucket_public_access_block" "terraform_state" {
  bucket = aws_s3_bucket.terraform_state.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# DynamoDB 테이블 (State 잠금용)
resource "aws_dynamodb_table" "terraform_state_lock" {
  name           = "terraform-state-lock"
  billing_mode   = "PROVISIONED"
  hash_key       = "LockID"
  read_capacity  = 5
  write_capacity = 5

  attribute {
    name = "LockID"
    type = "S"
  }

  tags = {
    Name        = "Terraform State Lock"
    Environment = "dev"
    Purpose     = "terraform-state-lock"
  }
}
