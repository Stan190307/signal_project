## Alert Generation System 

The Alert Generation System evaluates patient data and alerts medical staff 
when a patient exceeds their personalized thresholds.
Most classes are separated because each class has a single responsibility. 
The AlertGenerator only evaluates data, so it does not handle notifications. 
The AlertManager is responsible for notifying medical staff, but it does not manage patient thresholds. 
This separation improves maintainability, because if a patient's threshold changes, 
only the AlertRule class needs to be updated.

- Relationships between the classes 
The AlertGenerator has associations with AlertManager and Alert, shown as solid lines, because it 
maintains a structural relationship with them. It uses these classes as part of its normal operation.
The AlertGenerator also has a dependency on the Patient class, shown as a dashed arrow, 
because it only uses Patient objects temporarily when evaluating alerts.
The DataStorage class has an association with the Patient class with multiplicity 1..*, meaning it stores one or more Patient objects.
The AlertRule class was introduced to improve maintainability. The Patient class has a composition relationship with AlertRule, 
meaning each AlertRule is created and managed by a Patient and cannot exist independently
By separating alert rules from the Patient class, each patient can have different rules without affecting other classes.

- Overall, this design keeps responsibilities separated and improves maintainability and flexibility.

## Data Storage System
The Data Access Layer securely stores and retrieves data. The data should have timestamps and different versions.
To achieve this, I created separate classes to reduce responsibilities, ensuring that each class has one job.
In this model, there is a DataStorage interface and a SecureDataStorage class. This allows data to be stored without the system needing to know the underlying implementation,
because the DataStorage interface defines the required behaviour.

- Relationships between the classes
The dashed arrow between the SecureDataStorage class and the DataStorage interface represents an inheritance relationship (implements interface). This means that SecureDataStorage implements the DataStorage interface.
The SecureDataStorage class has an association with the AccessControl class because it needs to check permissions when someone tries to access the data.
There is also a separate DeletionPolicy class. This follows the Single Responsibility Principle, making the system easier to maintain and allowing the deletion policy to be changed in the future without affecting other classes.
The DataRetriever class has an association with the DataStorage interface because it handles data queries.

Overall this design improves security and flexibility by separating responsibilities and using abstraction to hide internal details

## Patient Identification System 
The Patient Identification System links incoming data to the correct patient and handles mismatches and special cases.
Firstly, I created a class for the incoming data because the assignment did not explicitly specify where this data should come from.

- Relationships between the classes
The IncomingPatientData class has a dependency on the PatientIdentifier class because it only temporarily uses this class to match incoming data with the correct patient.
When no match is found, the PatientIdentifier class calls the noMatch method.
The hospital patient data is stored in a separate class to keep responsibilities separated. This data is used by the IdentityManager class, which manages patient mismatches and anomalies.
The IdentityManager class has an aggregation relationship with the HospitalPatient class. This means that the IdentityManager uses HospitalPatient objects, but the patients can exist independently of the manager. This creates a weak relationship between the two classes.
The IdentityManager class also has a dependency relationship with the PatientIdentifier class because it temporarily uses it to evaluate mismatches detected during patient identification.
Overall this design separates responsibilities and tries to improve flexibility within the identification system

## Data Access Layer
The Data Access Layer is responsible for retrieving and parsing data into multiple usable objects. In this design, 
I added the PatientData class to make clear the source of the incoming data.

- Relationships between the classes
The DataListener was made into an interface, making it easier to abstract the behaviour of different data listener implementations. 
For this reason, the subclasses TCPDataListener, WebSocketDataListener, and FileDataListener all implement the DataListener interface. 
This improves flexibility, because new listener types can easily be added in the future without changing existing code.
The DataSourceAdapter acts as the central component of the system. It listens for incoming data, sends raw data to the DataParser, 
and forwards parsed data to the DataStorage class. This separates responsibilities between receiving, processing, and storing data, 
ensuring that each class has a single responsibility. The DataParser creates PatientData objects from raw incoming data, creating an association between the two classes because of their structural relationship.
The DataStorage class stores patient records and provides methods to retrieve patient information and records when needed. 
The separation between listening and parsing through data helps maintainability between the classes. 
Overall, this design improves flexibility and abstraction by separating responsibilities and reducing dependencies between classes.
