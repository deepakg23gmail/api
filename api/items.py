from flask_injector import inject

from services.provider.ItemsProvider import ItemsProvider

@inject(data_provider=ItemsProvider)
def search(data_provider) -> list:
    return data_provider.get()