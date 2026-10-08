from decimal import Decimal
import pytest
from estoque.domain import QuantidadeInteira, QuantidadeFracionada, EntradaEstoque, SaidaEstoque, AjusteEstoque, BalancaAdapter, ValidationError


def test_quantidades_e_operacoes():
    assert QuantidadeInteira().validar('2') == 2
    assert QuantidadeFracionada().validar('0.125') == Decimal('.125')
    assert EntradaEstoque().executar(2,3)==5
    assert SaidaEstoque().executar(2,2)==0
    assert AjusteEstoque().executar(2,0)==0
    for value in ('NaN','Infinity','-1','0.0001','1000000000000'):
        with pytest.raises(ValidationError): QuantidadeFracionada().validar(value)
    with pytest.raises(ValidationError): QuantidadeInteira().validar('1.5')
    with pytest.raises(ValidationError): SaidaEstoque().executar(1,2)
    with pytest.raises(ValidationError): AjusteEstoque().executar(2,2)


def test_balanca_desconta_tara_com_precisao():
    assert BalancaAdapter().converter({'gross':'1.250','tare':'.250'},'19.90') == {
        'quantity':Decimal('1.000'),'subtotal':Decimal('19.90')}
    with pytest.raises(ValidationError): BalancaAdapter().converter({'gross':1,'tare':2},5)
