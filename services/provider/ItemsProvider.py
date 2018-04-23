import orm


class ItemsProvider(object):
    def __init__(self, items: list=[]):
        self._items = items

    def __init__(self, object):
        self._object = object
    """"This get method is a mock for test purpose"""
    def get(self, number_of_items: int=5) -> list:
        if not self._items:
            return []

        if number_of_items > len(self._items):
            number_of_items = len(self._items)

        return self._items[0:number_of_items]

    """get records from db"""

    def get(self) -> list:
        self.put()
        items = self._object.query(orm.Items).all()
        return [item.dump() for item in items]

    """put method is written to just help populate few records in db"""
    def put(self):
        self._items = orm.Items(id='1', name='Deepak')
        self._object.add(self._items)
        self._object.commit()
        self._items = orm.Items(id='2', name='Jagan')
        self._object.add(self._items)
        self._object.commit()


