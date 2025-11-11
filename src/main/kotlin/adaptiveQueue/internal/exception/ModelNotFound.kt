package ru.bmstu.naburnm8.adaptiveQueue.internal.exception

class ModelNotFound (model: String) : Exception("Model not found:\n $model")