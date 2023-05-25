import { add, findByEmail } from "../models/user.js";
import bcrypt from 'bcrypt';
import jwt from 'jsonwebtoken';
import { secret_key } from "../config.js";

export async function signup(req, res, next) {
    console.log("here")
    const { username, email, password } = req.body;
    if (!username || !password) {
        return res.status(400).send({ message: 'Username and password are required' });
    }
    try {
        const hashedPassword = await bcrypt.hash(password, 10);
        const user = { username, email, password: hashedPassword };
        const result = await add(user);
        const token = jwt.sign({ ...result, password: '' }, secret_key, { expiresIn: '1h' });
        res.send({
            success: true,
            token: token
        });

    } catch (err) {
        next(err);
    }
}

export async function signin(req, res, next) {

    const { email, password } = req.body;
    if (!email || !password) {
        return res.status(400).send({ message: 'Username and password are required' });
    }
    try {
        const user = await findByEmail(email);
        if (!user) {
            return res.status(401).send({ message: 'Invalid username or password' });
        }
        const match = await bcrypt.compare(password, user.password);
        if (!match) {
            return res.status(401).send({ message: 'Invalid username or password' });
        }

        const token = jwt.sign({ ...user, password: '' }, secret_key, { expiresIn: '1h' });
        res.send({
            success: true,
            token: token
        });

    } catch (err) {
        next(err);
    }
}