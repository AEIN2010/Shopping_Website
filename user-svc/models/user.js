import { Schema, model } from "mongoose";

const user_schema = new Schema({
    username: { type: String, required: true },
    email: { type: String, required: true, unique: true },
    password: { type: String, required: true },
});

const User = model('User', user_schema);

export async function findByEmail(email) {
    return await User.findOne({ email: email }).lean();
}

export async function add(user) {
    const _user = new User(user);
    const result = await _user.save();
    return result._doc;
}