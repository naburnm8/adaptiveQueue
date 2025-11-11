package ru.bmstu.naburnm8.adaptiveQueue.internal.exception

class ParameterNotFoundException(model: String, parameter: String) : Exception("Parameter '$parameter' not found at model: $model")