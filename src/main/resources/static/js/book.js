
    urlAll = 'http://localhost:8081/api/book/?filter=all';
    urlSorted = 'http://localhost:8081/api/book/?filter=sorted';
    urlByType = 'http://localhost:8081/api/book/?filter=type&typeId=';
    urlByCount = 'http://localhost:8081/api/book/?filter=count_equals&count=';
    urlByName = 'http://localhost:8081/api/book/?filter=name&name=';
    urlEdit = 'http://localhost:8081/api/book/';
    urlDelete = 'http://localhost:8081/api/book/';
    urlAdd = 'http://localhost:8081/api/book/';
    urlOverDue = 'http://localhost:8081/api/journal/extraInfo/books?filter=overdue';

    urlBorrow = 'http://localhost:8081/api/journal/';

    const table = document.getElementById('books');
    const filter_btn = document.getElementById('filtered-table');
    const box = document.getElementById('books-box');
    const add_btn = document.getElementById('add-btn');

    // for adding
    const name_input = document.getElementById('name');
    const count_input = document.getElementById('count');
    const typeBook_input = document.getElementById('type');

    function createNode(element) {
        return document.createElement(element);
    }
    function append(parent, el) {
        return parent.appendChild(el);
    }
    function clear() {
        for (; table.getElementsByTagName('tr').length > 1;) {
            table.deleteRow(1);
        }
        name_input.value = '';
        count_input.value = '';
        typeBook_input.value = '';
    }
    function onDelete() {
        const currentUrl = urlDelete + this.id.split('-')[2];
        fetch(currentUrl, {
            method: 'DELETE'
        })
            .then(data => data.json())
            .then(function () {
                console.log('Удален');
                clear();
                get(urlAll);
            })
            .catch(function () {
                console.log('Не удален');
                clear();
                get(urlAll);
            });
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
                    let Name = createNode('td');
                    let Count = createNode('td');
                    let TypeBookId = createNode('td');
                    let edit_btn = createNode('button');
                    let delete_btn = createNode('button');
                    let borrow_link = createNode('a');

                    Name.innerHTML = `${data[i].name}`;
                    Count.innerHTML = `${data[i].count}`;
                    TypeBookId.innerHTML = `${data[i].typeBookId}`;

                    delete_btn.innerHTML = '-';
                    delete_btn.id = 'delete-btn-' + `${data[i].id}`;
                    delete_btn.className = "btn btn-primary";

                    edit_btn.innerHTML = 'edit';
                    edit_btn.id = 'edit-btn-' + `${data[i].id}`;
                    edit_btn.className = "btn btn-primary";

                    borrow_link.innerHTML = 'get';
                    borrow_link.id = 'get-' + `${data[i].id}`;

                    borrow_link.onclick = function () {
                        var years = prompt('Введите Ваш ID', 0);

                        let Record = {
                            "clientId": years,
                            "bookId": data[i].id,
                            "dateBegin": null,
                            "dateEnd": null,
                            "dateReturn": null
                        };

                        console.log(Record);
                        const sendPromise = fetch(urlBorrow, {
                            method: 'POST',
                            headers: {'Content-Type': 'application/json'},
                            body: JSON.stringify(Record)
                        });
                        console.log(sendPromise);

                        sendPromise
                            .then(function () {
                                console.log('Взята');
                            })
                            .catch(function (e) {
                                console.log('Не взята');
                            });
                    }
                    delete_btn.onclick = onDelete;
                    edit_btn.onclick = function () {

                        let Name = createNode('input');
                        let Count = createNode('input');
                        let TypeBookId = createNode('input');

                        Name.className = 'form-control';
                        Count.className = 'form-control';
                        TypeBookId.className = 'form-control';

                        Name.value = `${data[i].name}`;
                        Count.value = `${data[i].count}`;
                        TypeBookId.value = `${data[i].typeBookId}`;

                        let send_btn = createNode('button');
                        send_btn.innerHTML = '+';
                        send_btn.id = 'book-' + `${data[i].id}`;
                        send_btn.className = "btn btn-primary";

                        send_btn.onclick = async function () {
                            let typeBook = {
                                "name": Name.value,
                                "count": Count.value,
                                "typeBookId": TypeBookId.value
                            };

                            const sendPromise = fetch(urlEdit + send_btn.id.split('-')[1], {
                                method: 'PUT',
                                headers: {'Content-Type': 'application/json'},
                                body: JSON.stringify(typeBook)
                            });

                            sendPromise
                                .then(data => data.json())
                                .then(function () {
                                    console.log('Обновлена');
                                    Name.remove();
                                    Count.remove();
                                    TypeBookId.remove();
                                    send_btn.remove();
                                    clear();
                                    get(urlAll);
                                })
                                .catch(function (e) {
                                    console.log('Не обновлена');
                                    Name.remove();
                                    Count.remove();
                                    TypeBookId.remove();
                                    send_btn.remove();
                                    clear();
                                    get(urlAll);
                                });
                        }

                        append(box, Name);
                        append(box, Count);
                        append(box, TypeBookId);
                        append(box, send_btn);
                    }

                    let td_btn_delete = createNode('td');
                    append(td_btn_delete, delete_btn);
                    let td_btn_edit = createNode('td');
                    append(td_btn_edit, edit_btn);
                    let td_btn_borrow = createNode('td');
                    append(td_btn_borrow, borrow_link);

                    append(li, td_btn_delete);
                    append(li, Name);
                    append(li, Count);
                    append(li, TypeBookId);
                    append(li, td_btn_borrow);
                    append(li, td_btn_edit);
                    append(table, li);
                }
            })
            .catch(function (e) {
                console.log('Неизвестная ошибка');
            });
    }

    filter_btn.onclick = function() {
        const filter = document.getElementById('filter').value;
        const str = filter.split('=');
        switch (str[0]) {
            case 'sorted':
                get(urlSorted);
                break;
            case 'all':
                get(urlAll);
                break;
            case 'type':
                get(urlByType + str[1]);
                break;
            case 'count':
                get(urlByCount + str[1]);
                break;
            case 'name':
                get(urlByName + str[1]);
                break;
            case 'overdue':
                get(urlOverDue);
                break;
            default:
                alert('Unknown parameter');
        }

    }
    add_btn.onclick = function () {
        let typeBook = {
            "name": name_input.value,
            "count": count_input.value,
            "typeBookId": typeBook_input.value
        };

        const sendPromise = fetch(urlAdd, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(typeBook)
        });
        console.log(sendPromise);

        sendPromise
            .then(data => data.json())
            .then(function (data) {
                console.log('Добавлен');
                console.log(data);
                clear();
                get(urlAll);
            })
            .catch(function () {
                console.log('Не добавлен');
                clear();
                get(urlAll);
            });
    }

    clear();
    get(urlAll);