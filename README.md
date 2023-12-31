
# Spomoo Android App

This Android app was developed as part of my bachelor's thesis and tracks the user's physical activity and mental health.
It continuously records the acceleration and rotation sensors as well as the step counter of the Android smartphone. In addition, the user's sport activities can be saved by recording the time spent exercising and their mental health can be determined by answering a questionnaire. This data can be viewed locally in the app and sent to a server so that a research team can analyse the relation between physical and mental well-being from this data.
The app can be used in English and German.


## Usage

In order to use the app, you have to:
- Have SQL database with the tables described in https://github.com/juliusmuet/Spomoo/blob/main/ServerApi/SQL_Tables.txt
- Have a server containing the ServerAPI folder (the SQL_Tables.txt can be deleted)
- Adjust the database credentials in https://github.com/juliusmuet/Spomoo/blob/main/ServerApi/RestAPI/includes/Constants.php
- Adjust the HttpBasicAuthentication credentials in https://github.com/juliusmuet/Spomoo/blob/main/ServerApi/RestAPI/public/index.php
- Adjust the HttpBasicAuthentication credentials in https://github.com/juliusmuet/Spomoo/blob/main/App/app/src/main/java/com/example/spomoo/serverinteraction/RetrofitClient.java
- Adjust the server URL in https://github.com/juliusmuet/Spomoo/blob/main/App/app/src/main/java/com/example/spomoo/serverinteraction/RetrofitClient.java


## Screenshots

![Screenshot](https://github.com/juliusmuet/Spomoo/blob/main/App-Screenshots/Home_1.jpg?raw=true)
Homescreen of the App

![Screenshot](https://github.com/juliusmuet/Spomoo/blob/main/App-Screenshots/Questionnaire_1.jpg?raw=true)
Sample question of the questionnaire

![Screenshot](https://github.com/juliusmuet/Spomoo/blob/main/App-Screenshots/Sport_2.jpg?raw=true)
Recording a sport activity

![Screenshot](https://github.com/juliusmuet/Spomoo/blob/main/App-Screenshots/Data_1.jpg?raw=true)
Data screen of the app

More screenshots can be viewed here: https://github.com/juliusmuet/Spomoo/tree/main/App-Screenshots


# Authors

The prototype for this project was developed in cooperation with
- [@Johanna1313](https://github.com/Johanna1313)
- [@MissCaro](https://github.com/MissCaroo)
- [@Pero](https://github.com/Programmero187)
- [@TomRei1103](https://github.com/TomRei1103)

The final product was created only by me: [@Julius](https://github.com/juliusmuet)


## License

MIT-License

Copyright 2023 Julius Müther

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
