# AWS Lambda Review Service

A serverless REST API for managing reviews, built with Spring Boot 3, AWS Lambda, and API Gateway. This project provides a scalable, cost-effective backend for review management operations.

## Project Description

The AWS Lambda Review Service is a cloud-native application that enables users to create, read, update, and delete reviews through REST endpoints. The application is deployed as a serverless function on AWS Lambda and is exposed through Amazon API Gateway, eliminating the need to manage server infrastructure while providing automatic scaling and pay-per-use billing.

### Key Features

- **Review Management**: Full CRUD operations for reviews (Create, Read, Update, Delete)
- **RESTful API**: Standard REST endpoints for seamless integration
- **Serverless Architecture**: Runs on AWS Lambda with automatic scaling
- **Spring Boot 3**: Modern Spring Boot framework with latest features
- **Fast Cold Starts**: Optimized for Lambda performance with direct component imports
- **Health Checks**: Built-in ping endpoint for monitoring application health
- **Local Development**: SAM CLI support for local testing before deployment

## Technology Stack

- **Language**: Java 21
- **Framework**: Spring Boot 3.2.6
- **Build Tool**: Maven
- **Serverless**: AWS Lambda & API Gateway
- **Infrastructure as Code**: AWS SAM (Serverless Application Model)
- **Container**: AWS Serverless Java Container 2.0.2
- **Testing**: JUnit 5
- **Utilities**: Project Lombok

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java 21**: Required for building and running the application
- **Maven 3.6+**: For dependency management and building the project
- **AWS CLI**: For AWS account access and configuration
- **SAM CLI**: For local testing and deployment to AWS
- **Git**: For version control
- **cURL** or **Postman**: For testing API endpoints (optional)

## Project Structure

```
aws-lambda-review-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       ├── Application.java              # Spring Boot application entry point
│   │   │       ├── StreamLambdaHandler.java      # Lambda handler for API requests
│   │   │       ├── controller/
│   │   │       │   ├── PingController.java       # Health check endpoint
│   │   │       │   └── ReviewController.java     # Review CRUD endpoints
│   │   │       ├── model/
│   │   │       │   └── Review.java               # Review data model
│   │   │       ├── service/
│   │   │       │   └── ReviewService.java        # Business logic layer
│   │   │       └── fitness/                      # Additional domain classes
│   │   └── resources/
│   │       └── application.properties            # Spring Boot configuration
│   └── test/
│       └── java/com/
│           └── StreamLambdaHandlerTest.java      # Unit tests
├── pom.xml                                       # Maven configuration
├── template.yml                                  # SAM template for deployment
└── README.md                                     # This file
```

## Building the Project

### Using Maven

Clean and build the project:

```bash
mvn clean package
```

This command will:
- Download all dependencies
- Compile the source code
- Run unit tests
- Package the application into a JAR file

### Using SAM Build

For building and preparing for AWS Lambda deployment:

```bash
sam build
```

Output:
```
Building resource 'AwsLambdaReviewServiceFunction'
Running JavaMavenWorkflow:Maven
...
Build Succeeded

Built Artifacts  : .aws-sam/build
Built Template   : .aws-sam/build/template.yaml
```

## API Endpoints

The application exposes the following endpoints:

### Health Check
- **GET** `/ping` - Returns a ping response to verify the service is running
  ```bash
  curl http://localhost:3000/ping
  ```
  Response: `{ "pong": "Hello, World!" }`

### Review Endpoints
- **GET** `/reviews` - Retrieve all reviews
  ```bash
  curl http://localhost:3000/reviews
  ```

- **GET** `/reviews/{id}` - Retrieve a specific review by ID
  ```bash
  curl http://localhost:3000/reviews/1
  ```

- **POST** `/reviews` - Create a new review
  ```bash
  curl -X POST http://localhost:3000/reviews \
    -H "Content-Type: application/json" \
    -d '{"content": "Great product!", "rating": 5}'
  ```

- **PUT** `/reviews/{id}` - Update an existing review
  ```bash
  curl -X PUT http://localhost:3000/reviews/1 \
    -H "Content-Type: application/json" \
    -d '{"content": "Excellent!", "rating": 5}'
  ```

- **DELETE** `/reviews/{id}` - Delete a review
  ```bash
  curl -X DELETE http://localhost:3000/reviews/1
  ```

## Local Development & Testing

### Starting the Local Development Server

From the project root directory where `template.yml` is located, start the API:

```bash
sam local start-api
```

This will start a local API on `http://127.0.0.1:3000`

Expected output:
```
Mounting com.fitness.StreamLambdaHandler::handleRequest (java21) at http://127.0.0.1:3000/{proxy+}
```

### Testing Locally

In a new terminal, test the endpoints:

**Ping endpoint:**
```bash
curl -s http://127.0.0.1:3000/ping
```

**Get all reviews:**
```bash
curl -s http://127.0.0.1:3000/reviews
```

**Create a review:**
```bash
curl -s -X POST http://127.0.0.1:3000/reviews \
  -H "Content-Type: application/json" \
  -d '{"content": "Amazing service!", "rating": 5}'
```

### Running Unit Tests

Execute the test suite:

```bash
mvn test
```

Or run a specific test class:

```bash
mvn test -Dtest=StreamLambdaHandlerTest
```

## Deployment to AWS

### Prerequisites for Deployment
- AWS Account with appropriate credentials configured
- S3 bucket for SAM artifacts
- Permissions to create Lambda functions, API Gateway, and CloudFormation stacks

### Deploy with Guided Mode

The guided deployment walks you through the process interactively:

```bash
sam deploy --guided
```

You will be prompted for:
- Stack Name: The CloudFormation stack name (e.g., `aws-lambda-review-service`)
- AWS Region: Target deployment region (e.g., `us-east-1`)
- S3 Bucket: For SAM artifacts (auto-created if you enter a new name)
- Capabilities: Confirm to allow IAM role creation

### Deploy Without Guided Mode

If you've already configured deployment parameters:

```bash
sam deploy
```

### Verify Deployment

After successful deployment, SAM will output the API endpoint URL:

```
-------------------------------------------------------------------------------------------------------------
OutputKey-Description                        OutputValue
-------------------------------------------------------------------------------------------------------------
AwsLambdaReviewServiceApi - URL for application    https://xxxxx.execute-api.us-east-1.amazonaws.com/Prod/
-------------------------------------------------------------------------------------------------------------
```

### Testing the Deployed Application

Use the provided URL to test your deployed API:

```bash
curl -s https://xxxxx.execute-api.us-east-1.amazonaws.com/Prod/ping | python -m json.tool
```

Response:
```json
{
    "pong": "Hello, World!"
}
```

## Configuration

### Application Properties

Edit `src/main/resources/application.properties` to configure the application:

```properties
# Server configuration
server.port=8080

# Logging configuration
logging.level.root=INFO
logging.level.com.fitness=DEBUG
```

### Lambda Configuration

The Lambda function is configured in `template.yml`:

- **Runtime**: Java 21
- **Memory**: 512 MB
- **Timeout**: 30 seconds
- **Handler**: `com.fitness.StreamLambdaHandler::handleRequest`

## Troubleshooting

### Issue: Cold Start Performance
**Solution**: The application uses direct `@Import` instead of `@ComponentScan` to optimize cold starts. Ensure component scanning is kept minimal.

### Issue: Lambda Execution Role Errors
**Solution**: Ensure your IAM user has permissions to create Lambda functions, API Gateway, and CloudFormation stacks.

### Issue: Build Failures
**Solution**: 
- Verify Java 21 is installed: `java -version`
- Clear Maven cache: `mvn clean`
- Update Maven: `mvn --version`

### Issue: Local SAM Testing Fails
**Solution**:
- Ensure Docker is running (SAM requires Docker)
- Check SAM CLI installation: `sam --version`

## Performance Optimization

### Cold Start Optimization
- Direct component imports in `Application.java` reduce Spring Boot startup time
- Consider using AWS Lambda PowerTuning to find optimal memory settings

### Deployment Package Size
- Uses Maven assembly to create an optimized JAR
- Current package size is optimized for Lambda limits

## Security Considerations

- **API Gateway**: Consider adding authentication (API keys, AWS IAM, OAuth)
- **Lambda Permissions**: Use least-privilege IAM roles
- **Sensitive Data**: Use AWS Secrets Manager for credentials
- **Logging**: Monitor CloudWatch logs for suspicious activity
- **Validation**: Input validation is recommended in production

## Monitoring & Logging

### CloudWatch Logs

View logs for your Lambda function:

```bash
sam logs -n AwsLambdaReviewServiceFunction --stack-name aws-lambda-review-service
```

### Metrics

Monitor key metrics in CloudWatch:
- Invocations
- Duration
- Errors
- Throttles
- Concurrent Executions

## Contributing

1. Create a feature branch
2. Make your changes
3. Write/update tests
4. Submit a pull request

## License

This project is based on the AWS Serverless Java Container archetype. For details, see the [AWS Serverless Java Container repository](https://github.com/aws/serverless-java-container).

## Resources

- [AWS Serverless Java Container](https://github.com/aws/serverless-java-container)
- [AWS Lambda Documentation](https://docs.aws.amazon.com/lambda/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [SAM CLI Documentation](https://docs.aws.amazon.com/serverless-application-model/)
- [API Gateway Documentation](https://docs.aws.amazon.com/apigateway/)
