import dotenv from 'dotenv';
dotenv.config();

const mongoUrl = process.env.MONGO_URL;

export const config = {
    dbUrl: mongoUrl,
    options: {
        useNewUrlParser: true,
        useUnifiedTopology: true,
    },
};

export const secret_key = process.env.SECRET_KEY;