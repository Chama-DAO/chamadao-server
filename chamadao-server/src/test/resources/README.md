# Test Resources

This directory contains resources used for testing.

## test-id.jpg

For the KYC document upload test, you need to create a test image file named `test-id.jpg` in this directory.

You can create this file by:
1. Taking a screenshot or using any image editing software to create a simple image
2. Saving it as "test-id.jpg"
3. Placing it in this directory (src/test/resources)

Alternatively, you can modify the `KycControllerTests.java` file to use a different test file that you already have available.

## Note on Test Data

The test data provided in the Postman documentation (`postman_test_data.md`) can be used to manually test the API endpoints using Postman. The test files in the `src/test/java/com/chama/chamadao_server/tests` directory are for automated testing.

When running the automated tests, make sure you have the necessary test resources in place, such as the `test-id.jpg` file for KYC document upload testing.