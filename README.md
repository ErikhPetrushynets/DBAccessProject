<b>Language</b>: Java <br> 
<b>Project Name</b>: Using DBMS transactions in the application <br> <br> 
This program uses basic DML and TSL operations to query the database (Postgres). <br> <br> 
<img width="259" alt="Знімок екрана 2024-01-30 о 18 56 29" src="https://github.com/ErikhPetrushynets/DB_Lab_08/assets/132948467/1d0703bf-8f2d-4e7e-9cd0-36eac6d1186e">
<img width="259" alt="Знімок екрана 2024-01-30 о 18 58 42" src="https://github.com/ErikhPetrushynets/DB_Lab_08/assets/132948467/2bac6d78-e996-44f2-bd1c-5605eabab0d2"> <br> <br> 
Several anomalies that may occur during simultaneous execution of transactions were investigated:
- Phantom - a situation when during the execution of one transaction, the same query gives a different number of rows. To fix this anomaly, the transaction isolation level has been changed to Serializable because it completely isolates transactions and makes the result of parallel transactions appear as if they were executed sequentially.
- Serialization anomaly - the case when the result of successful execution of a group of transactions does not agree with all possible orders of execution of these transactions one by one. <br><br>
<img width="210" alt="Знімок екрана 2024-01-30 о 18 59 37" src="https://github.com/ErikhPetrushynets/DB_Lab_08/assets/132948467/b6eafd63-d01d-453d-adb6-dd23b2e5224b"> <br><br>
To solve this anomaly, the isolation of the transaction was changed to Serializable, after that Postgres generated a corresponding error. <br><br>
<img width="210" alt="Знімок екрана 2024-01-30 о 18 59 51" src="https://github.com/ErikhPetrushynets/DB_Lab_08/assets/132948467/6e592a73-e401-417f-b28f-9dbf0aaf23a7"> <br><br>
In this case (as advised by Postgres in the error message), the code has been supplemented with appropriate queries that will rollback and redo the transaction after one second. This scenario gave a positive result. <br><br>
<img width="210" alt="Знімок екрана 2024-01-30 о 19 00 07" src="https://github.com/ErikhPetrushynets/DB_Lab_08/assets/132948467/b21cfded-0a8f-445f-8e2a-e1626543781f"> <br> 
