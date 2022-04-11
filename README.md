# rest-library

Automation Library API. Java course work

## Этапы:

1. Перенести схему БД в MySQL Подключиться к БД посредством JDBC
2. Создать spring-boot-app Подключить сборщик gradle + подключить необходимые фреймворки
3. Создать Entities-> Repositories-> Services 20 запросов CRUD
4. Реализовать эндпоинты для взаимодействия с базой данных извне
5. Подключить Spring security Ограничить выполнение запросов неавторизованными пользователями только запросами на
   выборку
6. Создать Приложение, позволяющее пользователю взаимодействовать с rest api через команды в консоли или графический
интерфейс.

## About: 
### 1. TypeBook
   {  
    "id":1,  
     "name":"Novel",  
     "count":10,  
     "fine":10.0,  
     "dayCount":10  
   }

+ id       - идентификатор  
+ name     - название типа  
+ fine     - штраф за просроченный возврат  
+ dayCount - кол-во дней бесплатного пользования книгой  
+ count    - кол-во доступных книг в библиотеке  

Допустимо записывать в БД typeBook без fine, dayCount, count. Тогда по умолчанию:
fine=10  
dayCount=10  
count=0  

1. Запрос к пустой таблице 
> Типа не существует с id = ?
2. Запрос к несуществующей записи. 
> Типа не существует с id = ?
3. Добавление типа, который уже есть в системе 
> Тип с названием: Novel уже существует, тип не добавлен.
6. Попытка удалить тип книги, который используется системой
> Тип нельзя удалить, так как он используется в книгах.
7. Обновить запись в БД c не валидными данным по спецификации 
> Неправильные значения, тип не обновлен.
8. Невалидные данные для типа
    * без имени
    * fine, count < 0
    * dayCount <= 0   
    > Неправильные значения, тип не добавлен.

####POST:  
/api/typebook/

####PUT:  
/api/typebook/{id}  

####GET:
/api/typebook/{id}  
/api/typebook/?filter=all  
/api/typebook/?filter=sorted  
/api/typebook/?filter=fine_before&fine=  
/api/typebook/?filter=fine_after&fine=  
/api/typebook/?filter=name&name=';    

####DELETE:
/api/typebook/{id}  
/api/typebook/?filter=fine&fine=  
/api/typebook/?filter=count&count=  

### 2. Book

BookView:  
{  
   "id": 14,  
   "name": "War and Peace",  
   "count": 100,  
   "typeBookId": 1  
}  

+ id       - идентификатор  
+ name     - название книги  
+ count    - кол-во доступных книг в библиотеке  
+ typeBook - тип книги  
+ typeBookId - id of typeBook  

Все поля для записи в БД необходимо заполнить. 

1. Запрос к пустой таблице 
>  Такой книги с id =?
2. Запрос к несуществующей книги 
>  Такой книги с id =?
3. Добавление книги, который уже есть в системе 
>  Книга Anna Karenina уже существует c таким типом
4. Попытка удалить книгу, которая используется системой 
>  Книгу нельзя удалить, так как она используется в журнале
5. Обновить запись в БД c не валидными данным по спецификации 
>  Неправильные значения, книга не обновлена.
6. При добавлении книги, прибавляется их число к числу доступных книг [TypeBook.count]. 
7. При удалении книги, отнимается их число из [TypeBook.count].
8. Невалидные данные для типа:
   а. без имени 
   b. typeBookId не существует 
   c. count < 0   
9. > Неправильные значения, книга не добавлена.
   > Типа не существует с id =21. 

#### POST:
/api/book/  

#### PUT:  
/api/book/{id}  

#### GET:  
/api/book/{id}  
/api/book/?filter=all  
/api/book/?filter=sorted  
/api/book/?filter=type&typeId=  
/api/book/?filter=count_less&count=  
/api/book/?filter=count_equals&count=  
/api/book/?filter=name&name=  

#### DELETE:
/api/book/{id}  
/api/book/?filter=name&name=  
/api/book/?filter=type&typeId=  

### 3. Client  
ClientView:  
{  
"username": "username",  
"password": "1",  
"firstName":"Test",  
"lastName":"LastTest",  
"fatherName":"Noname",    
"passportSeria":"1234",  
"passportNum":"06"  
}  

+ username - обязательное поле [1,255]
+ password - обязательное поле [1,255]
+ firstName - обязательное поле [1,255]
+ lastName - [1,21]
+ fatherName - [1,21]
+ passportSeria - обязательное поле [1,21]
+ passportNum - обязательное поле [1,21]

1. пользователь без username, password, firstName, passportSeria, passportNum не может быть добавлен.
   > Использованы запрещенные символы.  
   > Клиент не может быть без имени.  
   > Имя или фамилия не заполнены.  
2. пользователь не добавиться, если логин уже занят или пара[passportSeria, passportNum] уже есть в БД
   > Пользователь с такими паспортными данными уже существует.  
   > Логин уже занят.  
3. запрос к несуществующему клиенту 
   > Такого клиента с id =? нет.
   > Клиента не существует с id: 11.

####POST:  
/api/client/  
/api/admin/  

####PUT:  
/api/client/  

####GET:  
/api/client/{id}             
/api/client/{id}/fullInfo   
/api/client/?filter=all  
/api/client/?filter=sorted  
/api/client/?filter=full_namesakes&firstName=X&lastName=X  

####DELETE:   
/api/client/{id}  
/api/client/?filter=by_first_name&firstName=  

### 4. Journal  
RecordView:  
{  
"clientId":1,  
"bookId::1,  
"dateBegin": 12.12.12  
"dateEnd": 13.12.13  
"dateReturn": 14.12.14  
}  
  
При добавлении записи, TypeBook.count и Book.count уменьшаются на 1. При возврате книги TypeBook.count и Book.count увеличиваются на 1.
Запись можно удалить, только есть книга возвращена.  

1. запрос к пустой таблице  
   > Такой записи не существует id: ?  
2. удалить запись с невозвращенной книги   
   > Невозможно удалить запись, так как книгу не вернули.  
3. добавить/обновить запись с неправильными полями   
   > Дата возврата не может быть из будущего: Tue Nov 21 16:28:06 MSK 2023  
   > Не корректные данные. Запись не заполнена до конца.  
4. Взять книгу, которой нет в наличии  
   > Книг больше нет.  

####POST:
/api/journal/  

####PUT:
/api/journal/{id}/{date}  

####GET:  
/api/journal/{id}  
/api/journal/?filter=all  
/api/journal/?filter=sorted  
/api/journal/?filter=by_client&clientId=  
/api/journal/?filter=by_book&bookId=  

/api/journal/extraInfo?filter=overdue  
/api/journal/extraInfo?filter=debtors  
/api/journal/extraInfo?filter=not_returned  
/api/journal/extraInfo?filter=not_returned_by_client&clientId=  

####DELETE:  
/api/journal/{id}  
/api/journal/?filter=by_client&clientId=  
/api/journal/?filter=by_book&bookId=  
