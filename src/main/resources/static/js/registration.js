

const btn_reg = document.getElementById('reg');
const url = 'http://localhost:8081/api/client/';

    let username      = document.getElementById('username');
    let password      = document.getElementById('password');
    let firstName     = document.getElementById('firstName');
    let lastName      = document.getElementById('lastName');
    let fatherName    = document.getElementById('fatherName');
    let passportSeria = document.getElementById('passportSeria');
    let passportNum   = document.getElementById('passportNum');

btn_reg.onclick = function () {

    let client = {
        "username": username.value,
        "password": password.value,
        "firstName": firstName.value,
        "lastName": lastName.value,
        "fatherName": fatherName.value,
        "passportSeria": passportSeria.value,
        "passportNum": passportNum.value,
    };

    console.log(client);

    const sendPromise = fetch(url, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(client)
    });

    sendPromise
        .then(function () {
            console.log('Зарегистрирован');
            username='';
            password='';
            firstName='';
            lastName='';
            fatherName='';
            passportSeria='';
            passportNum='';
            setTimeout(function(){
                window.location.href = 'http://localhost:8081/journal';
            }, 1000);
        })
        .catch(function () {
            username='';
            password='';
            firstName='';
            lastName='';
            fatherName='';
            passportSeria='';
            passportNum='';

        });
}