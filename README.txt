Finished on 22/02/2026

Postman was used to test API functionality through the use of Headers

1) Create employee

POST   -   http://localhost:8080/api/employees/createEmployee

(example)
Key		Value

employeeId	1
employeeName	John Doe
department	IT

• Creates and stores an employee entry



2) Fetch employee by ID

GET   -   http://localhost:8080/api/employees/fetch

(example)
Key		Value

employeeId	1

• Returns a stored employee by ID



3) Fetch all employees

GET   -   http://localhost:8080/api/employees/fetch/all

• Returns all stored employees



4) Delete employee by ID

DELETE   -   http://localhost:8080/api/employees/delete

(example)
Key		Value

employeeId	1

• Deletes a specified employee by ID



5) Delete all employees

DELETE   -   http://localhost:8080/api/employees/delete/all

• Deletes every employee entry

