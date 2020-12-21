const express = require('express');
const bodyParser = require('body-parser');
const crypto = require("crypto");
const cors = require('cors');
const app = express();
app.use(bodyParser.json());
app.use(cors());
const HDWalletProvider = require("@truffle/hdwallet-provider");
const twilio = require('twilio');
const MongoClient = require('mongodb').MongoClient

const rpcUrl = "https://rpc-mumbai.matic.today/"
let faucetProvider = new HDWalletProvider("97d8cb40d55f97fa4a9dcbb9d89b159128b95043edfd835467553f3b5c69d7af ", rpcUrl);
const Web3 = require('web3');
let web3 = new Web3("")


// Connecting to db
let users
const connectionString = "mongodb+srv://aman:aman@cluster0.btenx.mongodb.net/?retryWrites=true&w=majority";
MongoClient.connect(connectionString, {
    useUnifiedTopology: true
}, (err, client) => {
    if (err) return console.error(err)
    console.log('Connected to Database')
    const db = client.db('off');
    users = db.collection('users')
    // console.log(users);
})



// SignUp
app.get('/signup/:name/:phone/:password', async (req, res) => {

    const { name, phone, password } = req.params;
    // console.log(req.params)
    let newAccount = web3.eth.accounts.create()
    // console.log(JSON.stringify(newAccount))
    web3 = await new Web3(faucetProvider);
    // console.log(web3.eth.getAccounts(console.log))

    let accounts;
    await web3.eth.getAccounts().then(function (acc) { accounts = acc; });
    console.log(accounts[0])
    await web3.eth.sendTransaction({
        from: accounts[0],
        to: newAccount.address,
        value: '10000000000000000'
    }, (error, hash) => {
        if (error) {
            // console.log(error)
        } else {
            // console.log('hash: ' + hash)
        }
    }).then((receipt) => {
        // console.log(receipt)
    });
    // console.log({ "publicKey": newAccount.address, "privateKey": newAccount.privateKey.substring(2) })


    let ok = true;
    // check if already exists
    await users.findOne({ phone: phone })
        .then(results => {
            if (results != null) {
                ok = false;
            }
            console.log(results);
        })
        .catch(err => {
            console.log("here");
        })
    if (ok) {
        // insert
        await users.insert({
            name: name,
            phone: phone,
            password: password,
            public_key: newAccount.address,
            private_key: newAccount.privateKey.substring(2),
        })
            .catch(error => console.error(error))
        console.log("Added to db")
        res.send('Success');
    } else {
        console.log('Already exists');
        res.status(409).send('Already exists');
    }
})

// Login
app.get('/login/:phone/:password', (req, res) => {
    const { phone, password } = req.params;
    // console.log(users.insert);
    users.findOne({ phone: phone })
        .then(results => {
            if (results.password != password) {
                res.status(401).send("Authentication failed");
            }
            console.log(results);
            res.send(results.public_key + ':' + '1234567890ABCDEF');
        })
        .catch(err => {
            res.send(err);
        })
})

// Decrypt
app.post('/decrypt', async (req, res) => {
    const { message } = req.body;
    const ALGORITHM = 'aes-128-ecb';
    let DECRYPTION_KEY = "1234567890ABCDEF";
    let IV = Buffer.alloc(0);
    let decipher = crypto.createDecipheriv(ALGORITHM, DECRYPTION_KEY, IV);
    decipher.setAutoPadding(false);
    var encrypted = new Buffer(message, 'base64')
    let result = decipher.update(encrypted).toString();
    // console.log(result);
    res.send(result);
})


app.use('/twillioTrigger', async (req, res) => {
    const { access_token } = req.body;

    let accountSid = 'ACa7a06b4433795fe56454074098ff87e7';
    let authToken = '7fd06771216ff55532d3c4f48acc7edd';
    let client = new twilio(accountSid, authToken);
    let respMessage = "";
    await client.messages.list({
        limit: 1
    })
        .then(messages => {
            respMessage = messages[0];
            // console.log(messages[0])
        })

    console.log(respMessage);
    // decrypt message 
    const { body, from } = respMessage;

    // console.log(message);
    let IV = Buffer.alloc(0);
    let decipher = crypto.createDecipheriv('aes-128-ecb', '1234567890ABCDEF', IV);
    decipher.setAutoPadding(false);
    var encrypted = new Buffer(body, 'base64')
    let resultMessage = decipher.update(encrypted).toString();
    console.log(resultMessage);

    // if : found -> publickey:amount -> createTransaction
    // else not found -> amount -> buyEther 
    if (resultMessage.indexOf(':') != -1) {
        resultMessage = resultMessage.split(':');
        const [receiverPublicKey, amount] = resultMessage;
        console.log(receiverPublicKey, amount, from);
        await users.findOne({ phone: from })
            .then(results => {
                createTransaction(results.private_key, receiverPublicKey, 1);
            })
            .catch(err => {
                console.log("phone number not found");
                res.status(409).send('phone number not found');
            })
    } else {
        console.log(": not found");
        // amount = resultMessage
        await users.findOne({ phone: from })
            .then(async results => {
                const url = 'https://api.fusionfabric.cloud/retail-us/me/p2p/v1/external-p2p-payments';
                // access_token = '';
                // Take money from bank
                const res = await fetch(url, {
                    method: 'get',
                    headers: new Headers({
                        Authorization: 'Bearer ' + access_token,
                        Accept: 'application/json',
                        'Content-Type': 'application/json'
                    }),
                    body: {
                        "payeeName": "Reserved Payee",
                        "payeeType": "Mobile",
                        "payeePhoneNumber": "5555572323"
                    }
                })
                console.log(res);
                // send acknowledgement
                await client.messages.create({
                    body: "Transaction Successful !!!",
                    to: from,
                    from: '+12517650405'
                })
                    .then((message) => console.log(message.sid));
                // give ether
                buyEther(results.public_key, resultMessage);
            })
            .catch(err => {
                console.log("phone number not found");
                await client.messages.create({
                    body: "phone number not found",
                    to: from,
                    from: '+12517650405'
                })
                    .then((message) => console.log(message.sid));
                res.status(409).send('phone number not found');
            })
    }
    res.status(201).send("Printed all logs");
});

async function buyEther(requesterPublicKey, amount) {
    web3 = await new Web3(faucetProvider);
    console.log(web3.eth.getAccounts(console.log))
    let accounts;
    await web3.eth.getAccounts().then(function (acc) { accounts = acc; });

    await web3.eth.sendTransaction({
        from: accounts[0],
        to: requesterPublicKey,
        value: amount
    }, (error, hash) => {
        if (error) {
            console.log(error)
            return 'error'
        } else {
            console.log(hash)
            return hash;
        }
    })
}

async function createTransaction(senderPrivateKey, receiverPublicKey, amount) {
    let sender = new HDWalletProvider(senderPrivateKey, rpcUrl);
    web3 = await new Web3(sender);
    console.log(web3.eth.getAccounts(console.log))
    let accounts;
    await web3.eth.getAccounts().then(function (acc) { accounts = acc; });

    await web3.eth.sendTransaction({
        from: accounts[0],
        to: receiverPublicKey,
        value: amount
    }, (error, hash) => {
        if (error) {
            console.log(error)
            return 'error'
        } else {
            console.log(hash)
            return hash;
        }
    })
}

// Main Server
app.use('/', (req, res) => {
    console.log('Here');
    res.status(201).send("Finastra App API");
});

app.listen(process.env.PORT || 4000, () => {
    console.log('Listening on 4000');
});
