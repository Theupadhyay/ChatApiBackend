# Chat API – Spring Boot + JWT + WebSocket (STOMP)

A real-time chat backend built with **Spring Boot**, **Spring Security (JWT)**, **JPA**, and **WebSocket (STOMP)**.  
It supports REST APIs for authentication and messaging, and a WebSocket channel for live chat updates.

---

## 🚀 Features
- **User Authentication** – JWT-based login & signup  
- **Chat Rooms** – create/join rooms  
- **Messaging** – send & retrieve messages  
- **WebSocket (STOMP)** – receive messages in real-time  
- **JWT-secured WebSocket handshake** – pass token via query param or header  
- **H2 In-Memory DB** for testing  

---

## 🛠️ Tech Stack
- Java 17+  
- Spring Boot 3  
- Spring Web, Spring Security, Spring Messaging  
- JPA + H2 Database  
- WebSocket / STOMP with SimpMessagingTemplate  
- Postman / Browser for testing  

---

## 📂 Project Structure

src/main/java/com/chatApi/chatapi/

├─ config/ # Security & WebSocket configs

├─ controller/ # REST controllers (auth, messages)

├─ entity/ # User, Message, ChatRoom

├─ repository/ # Spring Data JPA repositories

├─ service/ # Business logic

└─ websocket/ # ChatMessage DTO


---

## ⚙️ Getting Started

### 1. Clone & Build
```bash
git clone https://github.com/your-username/chatapi.git
cd chatapi
./mvnw clean install
./mvnw spring-boot:run
Server starts at http://localhost:8083.

3. H2 Console (Optional)

Visit http://localhost:8083/h2-console
Use JDBC URL: jdbc:h2:mem:testdb

🔑 Authentication Flow
Register User

POST /api/auth/register

Body:
{
  "username": "Abhishek",
  "email": "abhishek@example.com",
  "password": "12345"
}
Login User

POST /api/auth/login

Body:
Login User

POST /api/auth/login

Body:

{
  "username": "Abhishek",
  "password": "12345"
}
Response:

{
  "token": "your-jwt-token"
}

Use this token as:

Authorization: Bearer <your-jwt-token>

📬 REST APIs
1️⃣ Send Message

POST /api/messages/send?roomId=1

Body:

Hello everyone!


Header:

Authorization: Bearer <token>

2️⃣ Get Message History

GET /api/messages/history/{roomId}

Header:

Authorization: Bearer <token>

🔔 WebSocket / STOMP
1. Connect to WebSocket

Connect to:

ws://localhost:8083/ws-chat?access_token=<jwt>

2. Subscribe to a Room

Client subscribes to:

/topic/rooms.{roomId}

3. Send Message via STOMP

Send to:

/app/chat.sendMessage


Example payload:

{
  "roomId": 1,
  "senderId": 2,
  "content": "Hello Room!"
}

4. Receive Message

Incoming messages appear on the subscribed topic:

/topic/rooms.1

🧪 Testing with Postman

Auth: Get JWT from /api/auth/login

REST: Use Authorization: Bearer <token> for all REST calls

WebSocket: In Postman “New → WebSocket Request”:

URL: ws://localhost:8083/ws-chat?access_token=<jwt>

Connect

Subscribe to /topic/rooms.1

Send message to /app/chat.sendMessage

📝 Notes

JWT can be passed via Authorization header or access_token query param for WebSocket.

Removed .withSockJS() in WebSocketConfig for pure WS connection testing.

Default DB resets at every restart (H2 in-memory).
