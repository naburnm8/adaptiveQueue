package ru.bmstu.naburnm8.adaptiveQueue.internal.exception

class ModelNotFoundException (model: String) : Exception("Model not found:\n $model")