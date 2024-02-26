provider "aws" {
  region = "ap-southeast-1"
}

module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "~> 3.0"

  name = "skl-node-js-postgresql-crud-example-vpc"
  cidr = "10.0.0.0/16"

  azs             = ["ap-southeast-1a", "ap-southeast-1b", "ap-southeast-1c"]
  private_subnets = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
  public_subnets  = ["10.0.101.0/24", "10.0.102.0/24", "10.0.103.0/24"]
}

resource "aws_db_instance" "skl_node_js_postgresql_crud_example_db" {
  identifier             = "skl-node-js-postgresql-crud-example-db"
  allocated_storage      = 20
  engine                 = "postgres"
  engine_version         = "12.5"
  instance_class         = "db.t2.micro"
  name                   = "skl-node-js-postgresql-crud-example-db"
  username               = "admin"
  password               = "password"
  parameter_group_name   = "default.postgres12"
  publicly_accessible    = true

  tags = {
    Name = "skl-node-js-postgresql-crud-example-db"
  }
}

resource "aws_eks_cluster" "skl_node_js_postgresql_crud_example_cluster" {
  name     = "skl-node-js-postgresql-crud-example-cluster"
  role_arn = aws_iam_role.skl_node_js_postgresql_crud_example.arn

  vpc_config {
    subnet_ids = module.skl_node_js_postgresql_crud_example_vpc.public_subnet_ids
  }
}

resource "aws_iam_role" "skl_node_js_postgresql_crud_example_eks_role" {
  name = "skl-node-js-postgresql-crud-example-eks-role"
  assume_role_policy = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Effect" : "Allow",
        "Principal" : {
          "Service" : "eks.amazonaws.com"
        },
        "Action" : "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_eip" "skl_node_js_postgresql_crud_example_eip" {
  vpc = true
}

resource "aws_nat_gateway" "skl_node_js_postgresql_crud_example" {
  allocation_id = aws_eip.skl_node_js_postgresql_crud_example.id
  subnet_id     = module.skl_node_js_postgresql_crud_example_vpc.public_subnet_ids[0]
}

resource "aws_route_table_association" "skl_node_js_postgresql_crud_example" {
  subnet_id      = module.skl_node_js_postgresql_crud_example_vpc.public_subnet_ids[0]
  route_table_id = module.skl_node_js_postgresql_crud_example_vpc.public_subnet_route_table_ids[0]
}