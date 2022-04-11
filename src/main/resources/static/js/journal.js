
urlById ='http://localhost:8081/api/journal/';
urlAll = 'http://localhost:8081/api/journal/?filter=all';
urlSorted = 'http://localhost:8081/api/journal/?filter=sorted';
urlByClient = 'http://localhost:8081/api/journal/?filter=by_client&clientId=';
urlByBook = 'http://localhost:8081/api/journal/?filter=by_book&bookId=';

const table = document.getElementById('journal');
const filter_btn = document.getElementById('filtered-table');

function createNode(element) {
    return document.createElement(element);
}
function append(parent, el) {
    return parent.appendChild(el);
}
function yyyymmdd(str){
    let ar = str.split('-');
    return ar[0]+'-'+ar[1]+'-'+ar[2].slice(0,2);
}
function clear() {
    for (; table.getElementsByTagName('tr').length > 1;) {
        table.deleteRow(1);
    }
}

function get(url) {
    clear();
    const myPromise = fetch(url);
    myPromise
        .then(data => data.json())
        .then(function (data) {
            console.log(data.length);
            for (let i = 0; i < data.length; i++) {

                let li = createNode('tr');
                let Id = createNode('td');
                let ClientId = createNode('td');
                let BookId = createNode('td');
                let Begin = createNode('td');
                let End = createNode('td');
                let Return;
                if (data[i].dateReturn==null)
                {
                    Return = createNode('button');
                    Return.innerHTML = 'ret';
                    Return.id = 'return-btn-' + `${data[i].id}`;
                    Return.className = "btn btn-primary";
                    Return.onclick = function (){

                            let Record = {
                                "clientId": data[i].clientId, //???
                                "bookId": data[i].bookId,
                                "dateBegin": data[i].dateBegin,
                                "dateEnd": data[i].dateEnd,
                                "dateReturn": null
                            };

                        const sendPromise = fetch(urlById + data[i].id, {
                            method: 'PUT',
                            headers: {'Content-Type': 'application/json'},
                            body: JSON.stringify(Record)
                        });

                        sendPromise
                            .then(function () {
                                console.log('Вернул');
                            })
                            .catch(function () {
                                console.log('Не вернул');
                            });
                    }
                }
                else {
                    Return = createNode('td');
                    Return.innerHTML = yyyymmdd(data[i].dateReturn);
                }

                Id.innerHTML = `${data[i].id}`;
                ClientId.innerHTML = `${data[i].clientId}`;
                BookId.innerHTML = `${data[i].bookId}`;
                Begin.innerHTML = yyyymmdd(data[i].dateBegin);
                End.innerHTML = yyyymmdd(data[i].dateEnd);

                let td_btn_ret = createNode('td');
                append(td_btn_ret, Return);

                append(li, Id);
                append(li, ClientId);
                append(li, BookId);
                append(li, Begin);
                append(li, End);
                append(li, td_btn_ret);
                append(table, li);
            }
        })
        .catch(function () {
            console.log("Неизвестная ошибка");
        });
}

filter_btn.onclick = function() {
    const filter = document.getElementById('filter').value;
    const str = filter.split('=');
    switch (str[0]) {
        case 'id':
            get(urlById + str[1]);
            break;
        case 'all':
            get(urlAll);
            break;
        case 'sorted':
            get(urlSorted);
            break;
        case 'by_client':
            get(urlByClient + str[1]);
            break;
        case 'by_book':
            get(urlByBook + str[1]);
            break;
        default:
            alert('Unknown parameter');
    }
}

clear();
get(urlAll);
