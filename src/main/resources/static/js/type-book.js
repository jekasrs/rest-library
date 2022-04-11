
    urlAll = 'http://localhost:8081/api/typebook/?filter=all';
    urlSorted = 'http://localhost:8081/api/typebook/?filter=sorted';
    urlFineBefore = 'http://localhost:8081/api/typebook/?filter=fine_before&fine=';
    urlFineAfter = 'http://localhost:8081/api/typebook/?filter=fine_after&fine=';
    urlByName = 'http://localhost:8081/api/typebook/?filter=name&name=';
    urlEdit = 'http://localhost:8081/api/typebook/';
    urlDelete = 'http://localhost:8081/api/typebook/';
    urlAdd = 'http://localhost:8081/api/typebook/';

    const table = document.getElementById('typebooks');
    const filter_btn = document.getElementById('filtered-table');
    const box = document.getElementById('typebooks-box');
    const add_btn = document.getElementById('add-btn');

    // for adding
    const name_input = document.getElementById('name');
    const fine_input = document.getElementById('fine');
    const count_input = document.getElementById('count');
    const dayCount_input = document.getElementById('dayCount');

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
        fine_input.value = '';
        count_input.value = '';
        dayCount_input.value = '';
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
            .catch(function (e) {
                console.log('не удален');
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
                    let Fine = createNode('td');
                    let Count = createNode('td');
                    let DayCount = createNode('td');
                    let edit_btn = createNode('button');
                    let delete_btn = createNode('button');

                    Name.innerHTML = `${data[i].name}`;
                    Fine.innerHTML = `${data[i].fine}`;
                    Count.innerHTML = `${data[i].count}`;
                    DayCount.innerHTML = `${data[i].dayCount}`;

                    delete_btn.innerHTML = '-';
                    delete_btn.id = 'delete-btn-' + data[i].id;
                    delete_btn.className = "btn btn-primary";
                    edit_btn.innerHTML = 'edit';
                    edit_btn.id = 'edit-btn-' + data[i].id;
                    edit_btn.className = "btn btn-primary";

                    delete_btn.onclick = onDelete;
                    edit_btn.onclick = function () {

                        let Name = createNode('input');
                        let Fine = createNode('input');
                        let Count = createNode('input');
                        let DayCount = createNode('input');

                        Name.className = 'form-control';
                        Fine.className = 'form-control';
                        Count.className = 'form-control';
                        DayCount.className = 'form-control';

                        Name.value = `${data[i].name}`;
                        Fine.value = `${data[i].fine}`;
                        Count.value = `${data[i].count}`;
                        DayCount.value = `${data[i].dayCount}`;

                        let send_btn = createNode('button');
                        send_btn.innerHTML = '+';
                        send_btn.id = `${data[i].id}`;
                        send_btn.className = "btn btn-primary";

                        send_btn.onclick = async function () {
                            let typeBook = {
                                "name": Name.value,
                                "fine": Fine.value,
                                "count": Count.value,
                                "dayCount": DayCount.value
                            };

                            const sendPromise = fetch(urlEdit + send_btn.id, {
                                method: 'PUT',
                                headers: {'Content-Type': 'application/json'},
                                body: JSON.stringify(typeBook)
                            });

                            sendPromise
                                .then(data => data.json())
                                .then(function () {
                                    console.log('Обновлен');
                                    Name.remove();
                                    Fine.remove();
                                    Count.remove();
                                    DayCount.remove();
                                    send_btn.remove();
                                    clear();
                                    get(urlAll);
                                })
                                .catch(function () {
                                    console.log('Не обновлен');
                                    Name.remove();
                                    Fine.remove();
                                    Count.remove();
                                    DayCount.remove();
                                    send_btn.remove();
                                    clear();
                                    get(urlAll);
                                });
                        }

                        append(box, Name);
                        append(box, Fine);
                        append(box, Count);
                        append(box, DayCount);
                        append(box, send_btn);
                    }

                    let td_btn_delete = createNode('td');
                    append(td_btn_delete, delete_btn);
                    let td_btn_edit = createNode('td');
                    append(td_btn_edit, edit_btn);
                    append(li, td_btn_delete);
                    append(li, Name);
                    append(li, Fine);
                    append(li, Count);
                    append(li, DayCount);
                    append(li, td_btn_edit);
                    append(table, li);
                }
            })
            .catch(function (e) {
                console.log('Не известно');
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
            case 'fine_before':
                get(urlFineBefore + str[1]);
                break;
            case 'fine_after':
                get(urlFineAfter + str[1]);
                break;
            case 'name':
                get(urlByName + str[1]);
                break;
            default:
                alert('Unknown parameter');
        }

    }
    add_btn.onclick = function () {
        let typeBook = {
            "name": name_input.value,
            "fine": fine_input.value,
            "count": count_input.value,
            "dayCount": dayCount_input.value
        };

        const sendPromise = fetch(urlAdd, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(typeBook)
        });

        sendPromise
            .then(data => data.json())
            .then(function (data) {
                console.log('Добавлен');
                console.log(data);
                clear();
                get(urlAll);
            })
            .catch(function (e) {
                console.log('Не добавлен');
                clear();
                get(urlAll);
            });
    }

    clear();
    get(urlAll);
