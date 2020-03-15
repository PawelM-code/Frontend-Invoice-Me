# InvoiceMe App Vaadin
Frontend InvoiceMe App (Vaadin).<br />
App created to issue invoices.
Additionally, it enables:
* management of issued invoices
* generating invoice to pdf file
* product management
* searching for buyer data by NIP (connection with the Ministry of Finance API)
* checking EUR, USD and CHF exchange rates. (connection to the API of the National Bank of Poland)

See also Backend InvoiceMe App [https://github.com/PawelM-code/Invoice-creator-REST-API](https://github.com/PawelM-code/Invoice-creator-REST-API) 

## Demo running on Heroku
On Heroku you find a deployed version. [https://invoice-me-app.herokuapp.com/](https://invoice-me-app.herokuapp.com/)
<br />
To log in use login and password: admin

## Local installation
1. Clone this repo to your local machine using [https://github.com/PawelM-code/Frontend-Invoice-Me](https://github.com/PawelM-code/Frontend-Invoice-Me)
2. Change in config/InvoiceMeAddress.class variable APP_URL to http://localhost:8080/
3. Optionally, if you also run the backed application locally, also change the variable BACKEND_URL (set a unique port) e.g. http://localhost:8084/v1
3. Run app and try this demo with your local browser by calling the following URL [http://localhost:8080/](http://localhost:8080/)

## Technologies
Project is created with:
* Java
* Maven
* Vaadin
* Spring-boot
* Itext PDF
* Spring security