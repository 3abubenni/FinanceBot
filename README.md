### Financial Tracking Bot 💰

A smart Telegram bot for personal finance management. 
Built to provide clear financial insights and help you make informed decisions about your money.

### 🚀 Getting Started
Prerequisites:

- Java 17+
- PostgreSQL (or your preferred database)
- Telegram Bot Token from @BotFather

Token and database creds set in src/resources/application.yaml;
Before launching the bot, execute DDL sql scripts in your database in the sql folder.
To run the application in docker, you can select one of the Dockerfile options in the src/docker folder.

### 🏗️ Tech Stack
- Backend: Quarkus (Java)
- Database: PostgreSQL
- API: Telegram Bot API
- Build tool: Maven