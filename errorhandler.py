import json


def render_unauthorized(exception):
    return json.dumps({'error': 'You can not divide a number by zero'})


def render_customexception(exception):
    return json.dumps({'error': 'There is a custom error'})
