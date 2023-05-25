import { Router } from "express";
import { signup, signin } from "../controllers/userController.js";

const router = Router({ mergeParams: true });

router.post('/signup', signup);
router.post('/signin', signin);

export default router;