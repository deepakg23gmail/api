class Greet(object):

    def post_greeting(self, name: str) -> str:
        return 'Hello {name}'.format(name=name)

class_instance = Greet()