<p align="center"><img src="https://user-images.githubusercontent.com/42104907/102727068-6ae50680-4349-11eb-955f-5c2beab1c23b.png" align="center" width="450"></p>
<h1 align="center">Ethastra</h1>
<h4 align="center">Connecting the unbanked</h4>

## Live Links
- [Project Demo](https://www.youtube.com/watch?v=raN3xos1ev4)
- [Mobile App](https://github.com/BakaOtaku/Ethastra/releases)
- [Server API](https://offline-trading.herokuapp.com/)
- [Presentation File](https://github.com/BakaOtaku/Ethastra/blob/master/Ethastra.pdf)

## Inspiration:

Cryptocurrencies are revolutionizing the world trade. But a major drawback is that a user always needs internet connection to create a wallet and trade in cryptocurrency. This means that in areas with poor internet , cryptocurrency trade gets hindered as it may lead to poor user experience, transaction failure etc.

## What it does:
Our system Ethastra:
- Uses *Finastra's external-p2p-payments*   API for exchanging money between bank accounts for crypto trade.
- Uses mobile SMS to eliminate active internet connection.
- AES end to end encryption to ensure user privacy when transacting via Ethereum as if not encrypted intercepter can see the transaction details of the receiver and sender and link the mobile no. with the sender's wallet harming the privacy.
- Buy and Send Ether

## How I built it
We made a mobile app to give the functionality of buy and send ether which will create an AES end to end encrypted SMS and send it to a mobile number. This mobile number is connected to cloud server using Twilio API and redirects the requests to Node.js webhook which will interpret the SMS. 
If user wants to buy then it will fetch the mobile number of the SMS sender and his corresponding Finastra Bank Account using the mobile number and then deduct money from his account to our account and in return award him with respective amount of cryptocurrency. If user wants to send cryptocurrency then it will fetch receiver's public key and amount from decrypted SMS and then carry out the transaction.

<img src="https://user-images.githubusercontent.com/42104907/102727109-b5ff1980-4349-11eb-8200-7c9c485278ec.png" align="center" width="500">


## Tech Stack

<details>
	<summary>Blockchain</summary>
		<ul>
			<li>Ethereum </li>
			<li>Web3</li>
		</ul>
</details>

<details>
	<summary>Backend</summary>
		<ul>
			<li>Node.js</li>
      <li>Finastra Fusion Fabric</li>
			<li>Twilio SMS API</li>
		</ul>
</details>

<details>
	<summary>Mobile App</summary>
		<ul>
			<li>Android Studio</li>
			<li>Crypto</li>
		</ul>
</details>

## Team

- [ 👨🏻‍💻 Aniket Dixit](https://github.com/dixitaniket)
- [ 👨🏻‍🎓 Arpit Srivastava](https://github.com/fuzious)
- [ 👨🏻‍💻 Vishnu Rahar](https://github.com/vishnurahar)
- [ 👨🏻‍💻 Souhard Swami](https://github.com/souhardswami)
- [ 🌊 Aman Raj](https://github.com/AmanRaj1608)


---
