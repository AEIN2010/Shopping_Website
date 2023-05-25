import express from "express";
import morgan from "morgan";
import fs from "fs";
import userRouter from "./routers/userRouter.js";
import mongoose from "mongoose";
import cors from 'cors';
import { validateToken } from "./middlewares/auth.js";
import { config } from './config.js';

const app = express();

await mongoose.connect(config.dbUrl, config.options);
console.log("Connected to MongoDB")

app.disable('x-powered-by');
app.use(cors())

app.use(express.json());
app.use('/users', userRouter);

//app.use(validateToken);

app.get("/", (req, res, next) => console.log("home"))
app.all("*", (req, res, next) => {
    next(new Error("Not Found"));
});

app.use((err, req, res, next) => {
    res.status(500).json({
        success: false,
        message: err.message
    });
});

app.listen(8080, () => {
    console.log("application is listening on port 8080");
})