import json
import csv
import os
from datetime import datetime
from collections import defaultdict

class ExpenseTracker:
    def __init__(self, data_file='expenses.json'):
        self.data_file = data_file
        self.expenses = []
        self.categories = ['Food', 'Travel', 'Bills', 'Entertainment', 'Shopping', 'Healthcare', 'Other']
        self.load_data()

    def load_data(self):
        if os.path.exists(self.data_file):
            try:
                with open(self.data_file, 'r') as f:
                    self.expenses = json.load(f)
            except:
                self.expenses = []
        else:
            self.expenses = []

    def save_data(self):
        with open(self.data_file, 'w') as f:
            json.dump(self.expenses, f, indent=2)

    def add_expense(self, date, category, amount, description):
        if category not in self.categories:
            return False
        try:
            datetime.strptime(date, '%Y-%m-%d')
            float(amount)
        except ValueError:
            return False
        
        expense = {
            'date': date,
            'category': category,
            'amount': float(amount),
            'description': description
        }
        self.expenses.append(expense)
        self.save_data()
        return True

    def get_expenses_by_month(self, year, month):
        monthly_expenses = []
        for expense in self.expenses:
            exp_date = datetime.strptime(expense['date'], '%Y-%m-%d')
            if exp_date.year == year and exp_date.month == month:
                monthly_expenses.append(expense)
        return monthly_expenses

    def get_category_breakdown(self, year, month):
        monthly = self.get_expenses_by_month(year, month)
        breakdown = defaultdict(float)
        for expense in monthly:
            breakdown[expense['category']] += expense['amount']
        return dict(breakdown)

    def get_monthly_total(self, year, month):
        return sum(expense['amount'] for expense in self.get_expenses_by_month(year, month))

    def get_highest_spending_category(self, year, month):
        breakdown = self.get_category_breakdown(year, month)
        if not breakdown:
            return None, 0
        return max(breakdown, key=breakdown.get), breakdown[max(breakdown, key=breakdown.get)]

    def export_to_csv(self, filename='expenses.csv'):
        if not self.expenses:
            return False
        with open(filename, 'w', newline='') as f:
            writer = csv.writer(f)
            writer.writerow(['Date', 'Category', 'Amount', 'Description'])
            for expense in sorted(self.expenses, key=lambda x: x['date']):
                writer.writerow([expense['date'], expense['category'], expense['amount'], expense['description']])
        return True


class ExpenseTrackerCLI:
    def __init__(self):
        self.tracker = ExpenseTracker()

    def show_menu(self):
        print("\n[Smart Expense Tracker]")
        print("\t1. Add Expense")
        print("\t2. View Monthly Summary")
        print("\t3. Category Breakdown")
        print("\t4. List All Expenses")
        print("\t5. Export to CSV")
        print("\t6. Exit")

    def add_expense(self):
        print("\nAdd Expense")
        date = input(f"Date (YYYY-MM-DD)): ")
        print(f"Categories: {', '.join(self.tracker.categories)}")
        category = input("Category: ").strip()
        amount = input("Amount: ").strip()
        description = input("Description: ").strip()

        if self.tracker.add_expense(date, category, amount, description):
            print("Expense added successfully")
        else:
            print("Failed to add expense. Check inputs.")

    def view_summary(self):
        print("\nMonthly Summary")
        year = int(input(f"Year [{datetime.now().year}]: ") or datetime.now().year)
        month = int(input(f"Month [{datetime.now().month}]: ") or datetime.now().month)

        monthly = self.tracker.get_expenses_by_month(year, month)
        if not monthly:
            print("No expenses found for this month.")
            return

        total = self.tracker.get_monthly_total(year, month)
        breakdown = self.tracker.get_category_breakdown(year, month)
        highest_cat, highest_amt = self.tracker.get_highest_spending_category(year, month)

        print(f"\nMonthly Summary - {year}-{month:02d}")
        print(f"Total Expenses: Rs. {total:.2f}")
        print(f"Transactions: {len(monthly)}")
        if highest_cat:
            print(f"Top Category: {highest_cat} (Rs. {highest_amt:.2f})")
        
        print("\nCategory Breakdown:")
        print(f"{'Category':<15} {'Amount':>12}")
        print("-" * 27)
        for cat in sorted(breakdown.keys()):
            print(f"{cat:<15} Rs. {breakdown[cat]:>10.2f}")

    def category_breakdown(self):
        print("\nCategory Breakdown")
        year = int(input(f"Year [{datetime.now().year}]: ") or datetime.now().year)
        month = int(input(f"Month [{datetime.now().month}]: ") or datetime.now().month)

        breakdown = self.tracker.get_category_breakdown(year, month)
        if not breakdown:
            print("No expenses found.")
            return

        total = self.tracker.get_monthly_total(year, month)
        print(f"\n{year}-{month:02d} Breakdown (Total: Rs. {total:.2f})")
        print(f"{'Category':<15} {'Amount':>12} {'Percentage':>12}")
        print("-" * 40)
        for cat in sorted(breakdown.keys(), key=lambda x: breakdown[x], reverse=True):
            pct = (breakdown[cat] / total * 100) if total > 0 else 0
            print(f"{cat:<15} Rs. {breakdown[cat]:>10.2f} {pct:>10.1f}%")

    def list_all_expenses(self):
        print("\nAll Expenses")
        if not self.tracker.expenses:
            print("No expenses recorded.")
            return

        print(f"{'Date':<12} {'Category':<15} {'Amount':>10} {'Description':<20}")
        print("-" * 60)
        for exp in sorted(self.tracker.expenses, key=lambda x: x['date'], reverse=True):
            desc = exp['description'][:20]
            print(f"{exp['date']:<12} {exp['category']:<15} Rs. {exp['amount']:>8.2f} {desc:<20}")

    def export_csv(self):
        filename = input("Filename (default: expenses.csv): ").strip() or "expenses.csv"
        if self.tracker.export_to_csv(filename):
            print(f"Exported to {filename}")
        else:
            print("Export failed")

    def run(self):
        while True:
            self.show_menu()
            choice = input("Choose option (1-6): ").strip()
            
            if choice == '1':
                self.add_expense()
            elif choice == '2':
                self.view_summary()
            elif choice == '3':
                self.category_breakdown()
            elif choice == '4':
                self.list_all_expenses()
            elif choice == '5':
                self.export_csv()
            elif choice == '6':
                print("Goodbye!")
                break
            else:
                print("Invalid option. Try again.")

if __name__ == '__main__':
    app = ExpenseTrackerCLI()
    app.run()
